package com.example.dangjang.domain.reservation.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.reservation.dto.AdminReservationListResponse;
import com.example.dangjang.domain.reservation.dto.AdminReservationSummaryResponse;
import com.example.dangjang.domain.reservation.entity.Reservation;
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
