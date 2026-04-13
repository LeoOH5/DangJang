package com.example.dangjang.domain.notification.repository;

import com.example.dangjang.domain.notification.entity.Notification;
import com.example.dangjang.domain.notification.entity.NotificationTargetType;
import com.example.dangjang.domain.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByUser_IdAndIsReadFalse(Long userId);

    boolean existsByTypeAndTargetTypeAndTargetId(
            NotificationType type,
            NotificationTargetType targetType,
            Long targetId
    );

    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.isRead = true where n.user.id = :userId and n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);
}
