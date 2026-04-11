package com.example.dangjang.domain.notification.scheduler;

import com.example.dangjang.domain.notification.entity.NotificationTargetType;
import com.example.dangjang.domain.notification.entity.NotificationType;
import com.example.dangjang.domain.notification.repository.NotificationRepository;
import com.example.dangjang.domain.notification.service.NotificationDispatchService;
import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickupReminderScheduler {

    private final ReservationRepository reservationRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatchService notificationDispatchService;

    @Scheduled(
            cron = "${app.notification.pickup-reminder-cron:0 0 9 * * ?}",
            zone = "${app.notification.pickup-reminder-zone:Asia/Seoul}"
    )
    @Transactional
    public void sendPickupReminders() {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        LocalDate pickupDate = LocalDate.now(zone).plusDays(1);
        List<Reservation> reservations = reservationRepository.findConfirmedByPickupDate(pickupDate);

        int sent = 0;
        for (Reservation reservation : reservations) {
            if (notificationRepository.existsByTypeAndTargetTypeAndTargetId(
                    NotificationType.PICKUP_REMINDER,
                    NotificationTargetType.RESERVATION,
                    reservation.getId()
            )) {
                continue;
            }
            notificationDispatchService.notifyPickupReminder(reservation);
            sent++;
        }
        if (sent > 0) {
            log.info("픽업 리마인더 알림 {}건 발송 (픽업일: {})", sent, pickupDate);
        }
    }
}
