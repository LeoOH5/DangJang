package com.example.dangjang.domain.reservation.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.repository.ProductRepository;
import com.example.dangjang.domain.reservation.dto.ReservationCancelResponse;
import com.example.dangjang.domain.reservation.dto.ReservationCreateRequest;
import com.example.dangjang.domain.reservation.dto.ReservationCreateResponse;
import com.example.dangjang.domain.reservation.dto.ReservationDetailResponse;
import com.example.dangjang.domain.reservation.dto.ReservationListResponse;
import com.example.dangjang.domain.reservation.dto.ReservationSummaryResponse;
import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.reservation.entity.ReservationItem;
import com.example.dangjang.domain.notification.service.NotificationDispatchService;
import com.example.dangjang.domain.recommendation.service.RecommendationScoreService;
import com.example.dangjang.domain.reservation.repository.ReservationRepository;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.repository.StoreRepository;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final DateTimeFormatter PICKUP_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final Set<String> RESERVATION_STATUSES = Set.of(
            "REQUESTED",
            "CONFIRMED",
            "CANCELED",
            "CANCELLED",
            "COMPLETED",
            "REJECTED",
            "READY_FOR_PICKUP"
    );

    private static final Set<String> CANCELLABLE_RESERVATION_STATUSES = Set.of("REQUESTED", "CONFIRMED");

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationDispatchService notificationDispatchService;
    private final RecommendationScoreService recommendationScoreService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationCreateResponse createReservation(String authorization, ReservationCreateRequest request) {
        validateRequiredFields(request);

        Long userId = authService.getAuthenticatedUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Reservation reservation = Reservation.builder()
                .user(user)
                .store(store)
                .pickupDate(request.getPickupDate())
                .pickupTime(request.getPickupTime())
                .requestNote(request.getRequestNote())
                .status("REQUESTED")
                .build();

        List<ReservationCreateResponse.ReservationItemResponse> responseItems = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (ReservationCreateRequest.ReservationItemCreateRequest itemRequest : request.getItems()) {
            Product product = productRepository.findByIdForReservation(itemRequest.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            if (!product.getStore().getId().equals(store.getId())) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            ProductDiscount discount = productDiscountRepository.findByIdForReservation(itemRequest.getProductDiscountId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_AVAILABLE));

            if (!discount.getProduct().getId().equals(product.getId()) || !discount.isActiveNow(now)) {
                throw new BusinessException(ErrorCode.DISCOUNT_NOT_AVAILABLE);
            }

            Integer quantity = itemRequest.getQuantity();
            if (quantity == null || quantity <= 0) {
                throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
            }

            product.decreaseStock(quantity);
            discount.decreaseRemainingQuantity(quantity);
            recommendationScoreService.trackReservation(discount.getId(), quantity);

            ReservationItem reservationItem = ReservationItem.builder()
                    .reservation(reservation)
                    .product(product)
                    .productDiscount(discount)
                    .quantity(quantity)
                    .discountPrice(discount.getDiscountPrice())
                    .build();

            reservation.addItem(reservationItem);
        }

        Reservation saved = reservationRepository.save(reservation);
        notificationDispatchService.notifyReservationRequested(saved);

        for (ReservationItem item : saved.getItems()) {
            responseItems.add(new ReservationCreateResponse.ReservationItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getDiscountPrice()
            ));
        }

        return new ReservationCreateResponse(
                saved.getId(),
                saved.getStatus(),
                saved.getStore().getId(),
                saved.getPickupDate(),
                saved.getPickupTime(),
                responseItems
        );
    }

    @Transactional(readOnly = true)
    public ReservationListResponse getMyReservations(String authorization, String status, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));

        Long userId = authService.getAuthenticatedUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        String statusFilter = status == null ? "" : status.trim();
        if (!statusFilter.isEmpty() && !RESERVATION_STATUSES.contains(statusFilter)) {
            throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Reservation> result = statusFilter.isEmpty()
                ? reservationRepository.findByUserOrderByIdDesc(user, pageable)
                : reservationRepository.findByUserAndStatusOrderByIdDesc(user, statusFilter, pageable);

        List<ReservationSummaryResponse> content = result.getContent().stream()
                .map(this::toSummary)
                .toList();

        return new ReservationListResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationDetail(String authorization, Long reservationId) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findDetailById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        List<ReservationDetailResponse.ReservationDetailItemResponse> items = reservation.getItems().stream()
                .map(this::toDetailItem)
                .toList();

        String note = reservation.getRequestNote() == null ? "" : reservation.getRequestNote();

        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getStore().getId(),
                reservation.getStore().getName(),
                reservation.getPickupDate().toString(),
                reservation.getPickupTime().format(PICKUP_TIME_FORMAT),
                note,
                items
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationCancelResponse cancelReservation(String authorization, Long reservationId) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findDetailById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        String current = reservation.getStatus();
        if ("CANCELED".equals(current) || "CANCELLED".equals(current)) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_CANCELED);
        }
        if (!CANCELLABLE_RESERVATION_STATUSES.contains(current)) {
            throw new BusinessException(ErrorCode.RESERVATION_CANNOT_CANCEL);
        }

        for (ReservationItem item : reservation.getItems()) {
            Product product = productRepository.findByIdForReservation(item.getProduct().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            product.increaseStock(item.getQuantity());
            if (item.getProductDiscount() != null) {
                ProductDiscount discount = productDiscountRepository.findByIdForReservation(item.getProductDiscount().getId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_DISCOUNT_NOT_FOUND));
                discount.increaseRemainingQuantity(item.getQuantity());
            }
        }

        reservation.changeStatus("CANCELED");

        return new ReservationCancelResponse(reservation.getId(), "CANCELED");
    }

    private ReservationDetailResponse.ReservationDetailItemResponse toDetailItem(ReservationItem item) {
        return new ReservationDetailResponse.ReservationDetailItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getOriginalPrice().intValue(),
                item.getDiscountPrice().intValue()
        );
    }

    private ReservationSummaryResponse toSummary(Reservation reservation) {
        int totalItemCount = reservation.getItems().stream()
                .mapToInt(ReservationItem::getQuantity)
                .sum();
        return new ReservationSummaryResponse(
                reservation.getId(),
                reservation.getStore().getId(),
                reservation.getStore().getName(),
                reservation.getStatus(),
                reservation.getPickupDate().toString(),
                reservation.getPickupTime().format(PICKUP_TIME_FORMAT),
                totalItemCount
        );
    }

    private void validateRequiredFields(ReservationCreateRequest request) {
        if (request == null
                || request.getStoreId() == null
                || request.getPickupDate() == null
                || request.getPickupTime() == null
                || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }
}
