package com.example.dangjang.domain.reservation.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.repository.ProductRepository;
import com.example.dangjang.domain.reservation.dto.ReservationCreateRequest;
import com.example.dangjang.domain.reservation.dto.ReservationCreateResponse;
import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.reservation.entity.ReservationItem;
import com.example.dangjang.domain.reservation.repository.ReservationRepository;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.repository.StoreRepository;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
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
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            if (!product.getStore().getId().equals(store.getId())) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            ProductDiscount discount = productDiscountRepository.findById(itemRequest.getProductDiscountId())
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
