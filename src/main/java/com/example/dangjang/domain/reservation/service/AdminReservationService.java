package com.example.dangjang.domain.reservation.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.reservation.dto.AdminReservationListResponse;
import com.example.dangjang.domain.reservation.dto.AdminReservationSummaryResponse;
import com.example.dangjang.domain.reservation.dto.ReservationConfirmResponse;
import com.example.dangjang.domain.reservation.dto.ReservationRejectRequest;
import com.example.dangjang.domain.reservation.dto.ReservationRejectResponse;
import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.reservation.entity.ReservationItem;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

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

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public AdminReservationListResponse getStoreReservations(
            String authorization,
            Long storeId,
            String status,
            int page,
            int size
    ) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        if (storeId == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (store.getOwner() == null || !store.getOwner().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.STORE_RESERVATION_ACCESS_DENIED);
        }

        String statusFilter = status == null ? "" : status.trim();
        if (!statusFilter.isEmpty() && !RESERVATION_STATUSES.contains(statusFilter)) {
            throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<Reservation> result = statusFilter.isEmpty()
                ? reservationRepository.findByStore_IdOrderByIdDesc(storeId, pageable)
                : reservationRepository.findByStore_IdAndStatusOrderByIdDesc(storeId, statusFilter, pageable);

        List<AdminReservationSummaryResponse> content = result.getContent().stream()
                .map(this::toAdminSummary)
                .toList();

        return new AdminReservationListResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional
    public ReservationConfirmResponse confirmReservation(String authorization, Long reservationId) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findWithStoreAndOwnerById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        Store store = reservation.getStore();
        if (store.getOwner() == null || !store.getOwner().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.STORE_RESERVATION_ACCESS_DENIED);
        }

        if (!"REQUESTED".equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_APPROVE_INVALID_STATUS);
        }

        reservation.changeStatus("CONFIRMED");
        return new ReservationConfirmResponse(reservation.getId(), "CONFIRMED");
    }

    @Transactional
    public ReservationRejectResponse rejectReservation(
            String authorization,
            Long reservationId,
            ReservationRejectRequest request
    ) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        String reason = request != null && request.getReason() != null ? request.getReason().trim() : "";
        if (reason.isEmpty()) {
            throw new BusinessException(ErrorCode.RESERVATION_REJECT_REASON_REQUIRED);
        }

        Reservation reservation = reservationRepository.findDetailById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        Store store = reservation.getStore();
        if (store.getOwner() == null || !store.getOwner().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.STORE_RESERVATION_ACCESS_DENIED);
        }

        if (!"REQUESTED".equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_REJECT_INVALID_STATUS);
        }

        for (ReservationItem item : reservation.getItems()) {
            item.getProduct().increaseStock(item.getQuantity());
            if (item.getProductDiscount() != null) {
                item.getProductDiscount().increaseRemainingQuantity(item.getQuantity());
            }
        }

        reservation.rejectWithReason(reason);
        return new ReservationRejectResponse(reservation.getId(), "REJECTED");
    }

    private AdminReservationSummaryResponse toAdminSummary(Reservation reservation) {
        User reservationUser = reservation.getUser();
        return new AdminReservationSummaryResponse(
                reservation.getId(),
                reservationUser.getId(),
                reservationUser.getName(),
                reservation.getStore().getId(),
                reservation.getStore().getName(),
                reservation.getStatus(),
                reservation.getPickupDate().toString(),
                reservation.getPickupTime().format(PICKUP_TIME_FORMAT)
        );
    }
}
