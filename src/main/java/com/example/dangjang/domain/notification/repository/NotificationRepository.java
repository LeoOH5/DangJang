package com.example.dangjang.domain.notification.repository;

import com.example.dangjang.domain.notification.entity.Notification;
import com.example.dangjang.domain.notification.entity.NotificationTargetType;
import com.example.dangjang.domain.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByTypeAndTargetTypeAndTargetId(
            NotificationType type,
            NotificationTargetType targetType,
            Long targetId
    );
}
