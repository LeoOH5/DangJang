package com.example.dangjang.domain.discount.scheduler;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductDiscountStatusScheduler {

    private final ProductDiscountRepository productDiscountRepository;

    @Scheduled(
            fixedDelayString = "${app.discount.status-check-interval-ms:60000}",
            initialDelayString = "${app.discount.status-check-initial-delay-ms:5000}"
    )
    @Transactional
    public void transitionDiscountStatuses() {
        LocalDateTime now = LocalDateTime.now();
        int endedCount = 0;
        int activatedCount = 0;

        for (ProductDiscount discount : productDiscountRepository.findByStatusAndEndAtBefore(DiscountStatus.ACTIVE, now)) {
            discount.changeStatus(DiscountStatus.ENDED);
            endedCount++;
        }
        for (ProductDiscount discount : productDiscountRepository.findByStatusAndEndAtBefore(DiscountStatus.SCHEDULED, now)) {
            discount.changeStatus(DiscountStatus.ENDED);
            endedCount++;
        }
        for (ProductDiscount discount : productDiscountRepository.findByStatusAndRemainingQuantity(DiscountStatus.ACTIVE, 0)) {
            discount.changeStatus(DiscountStatus.ENDED);
            endedCount++;
        }
        for (ProductDiscount discount : productDiscountRepository.findByStatusAndRemainingQuantity(DiscountStatus.SCHEDULED, 0)) {
            discount.changeStatus(DiscountStatus.ENDED);
            endedCount++;
        }
        for (ProductDiscount discount : productDiscountRepository
                .findByStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqualAndRemainingQuantityGreaterThan(
                        DiscountStatus.SCHEDULED, now, now, 0)) {
            discount.changeStatus(DiscountStatus.ACTIVE);
            activatedCount++;
        }

        if (endedCount > 0 || activatedCount > 0) {
            log.info("할인 자동 상태 변경 완료 — 종료 {}건, 활성화 {}건 (기준 시각: {})", endedCount, activatedCount, now);
        }
    }
}
