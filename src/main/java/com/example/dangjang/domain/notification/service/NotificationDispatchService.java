package com.example.dangjang.domain.notification.service;

import com.example.dangjang.domain.favorite.entity.Favorite;
import com.example.dangjang.domain.favorite.repository.FavoriteRepository;
import com.example.dangjang.domain.notification.entity.Notification;
import com.example.dangjang.domain.notification.entity.NotificationTargetType;
import com.example.dangjang.domain.notification.entity.NotificationType;
import com.example.dangjang.domain.notification.repository.NotificationRepository;
import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private static final DateTimeFormatter PICKUP_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final String SSE_EVENT_NAME = "notification";

    private final NotificationRepository notificationRepository;
    private final NotificationSseService notificationSseService;
    private final FavoriteRepository favoriteRepository;

    /** 사용자 예약 요청 → 매장 사장에게 알림 */
    public void notifyReservationRequested(Reservation reservation) {
        User owner = reservation.getStore().getOwner();
        if (owner == null) {
            return;
        }
        String content = String.format(
                "%s님이 %s에 예약을 요청했습니다.",
                reservation.getUser().getName(),
                reservation.getStore().getName()
        );
        dispatch(
                owner,
                NotificationType.RESERVATION_REQUESTED,
                "새 예약 요청",
                content,
                NotificationTargetType.RESERVATION,
                reservation.getId()
        );
    }

    /** 매장 예약 승인 → 예약 고객에게 알림 */
    public void notifyReservationConfirmed(Reservation reservation) {
        User customer = reservation.getUser();
        Store store = reservation.getStore();
        String content = String.format("%s 예약이 승인되었습니다.", store.getName());
        dispatch(
                customer,
                NotificationType.RESERVATION_CONFIRMED,
                "예약이 승인되었습니다.",
                content,
                NotificationTargetType.RESERVATION,
                reservation.getId()
        );
    }

    /** 매장 예약 거절 → 예약 고객에게 알림 */
    public void notifyReservationRejected(Reservation reservation) {
        User customer = reservation.getUser();
        Store store = reservation.getStore();
        String reason = reservation.getRejectReason() != null ? reservation.getRejectReason() : "";
        String content = String.format("%s 예약이 거절되었습니다. 사유: %s", store.getName(), reason);
        dispatch(
                customer,
                NotificationType.RESERVATION_REJECTED,
                "예약이 거절되었습니다.",
                content,
                NotificationTargetType.RESERVATION,
                reservation.getId()
        );
    }

    /** 할인 등록 → 해당 매장을 즐겨찾기한 사용자에게 알림 */
    public void notifyDiscountCreatedForStoreFavorites(Store store, Long productDiscountId, String discountTitle) {
        List<Favorite> favorites = favoriteRepository.findAllWithUserByStoreId(store.getId());
        if (favorites.isEmpty()) {
            return;
        }
        String content = String.format("%s에 새 할인이 등록되었습니다: %s", store.getName(), discountTitle);
        for (Favorite favorite : favorites) {
            dispatch(
                    favorite.getUser(),
                    NotificationType.DISCOUNT_CREATED,
                    "즐겨찾기 매장 할인 안내",
                    content,
                    NotificationTargetType.PRODUCT_DISCOUNT,
                    productDiscountId
            );
        }
    }

    /** 픽업 전일 시스템 리마인더 → 예약 고객에게 알림 */
    public void notifyPickupReminder(Reservation reservation) {
        String content = String.format(
                "%s 예약 픽업일은 %s %s 입니다.",
                reservation.getStore().getName(),
                reservation.getPickupDate().toString(),
                reservation.getPickupTime().format(PICKUP_TIME_FORMAT)
        );
        dispatch(
                reservation.getUser(),
                NotificationType.PICKUP_REMINDER,
                "픽업 일정 알림",
                content,
                NotificationTargetType.RESERVATION,
                reservation.getId()
        );
    }

    private void dispatch(
            User user,
            NotificationType type,
            String title,
            String content,
            NotificationTargetType targetType,
            Long targetId
    ) {
        Notification entity = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .isRead(false)
                .build();
        Notification saved = notificationRepository.save(entity);
        notificationRepository.flush();
        captureAndScheduleSse(saved);
    }

    private void captureAndScheduleSse(Notification saved) {
        Long userId = saved.getUser().getId();
        Long notificationId = saved.getId();
        String type = saved.getType().name();
        String title = saved.getTitle();
        String content = saved.getContent();
        Long targetId = saved.getTargetId();
        String targetTypeName = saved.getTargetType() != null ? saved.getTargetType().name() : null;
        LocalDateTime createdAt = saved.getCreatedAt();

        Runnable push = () -> sendSseJson(userId, notificationId, type, title, content, targetId, targetTypeName, createdAt);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    push.run();
                }
            });
        } else {
            push.run();
        }
    }

    private void sendSseJson(
            Long userId,
            Long notificationId,
            String type,
            String title,
            String content,
            Long targetId,
            String targetTypeName,
            LocalDateTime createdAt
    ) {
        String json = String.format(
                Locale.US,
                "{\"notificationId\":%d,\"type\":%s,\"title\":%s,\"content\":%s,\"targetId\":%s,\"targetType\":%s,\"isRead\":false,\"createdAt\":%s}",
                notificationId,
                quoteJson(type),
                quoteJson(title),
                quoteJson(content),
                targetId == null ? "null" : Long.toString(targetId),
                targetTypeName == null ? "null" : quoteJson(targetTypeName),
                createdAt == null ? "null" : quoteJson(createdAt.toString())
        );
        notificationSseService.sendToUser(userId, SSE_EVENT_NAME, json);
    }

    private static String quoteJson(String value) {
        if (value == null) {
            return "null";
        }
        String escaped = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        return "\"" + escaped + "\"";
    }
}
