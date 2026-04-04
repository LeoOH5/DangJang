package com.example.dangjang.domain.discount.repository;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    List<ProductDiscount> findByProductAndStatus(Product product, DiscountStatus status);

    List<ProductDiscount> findByProduct(Product product);

    @Query("""
            select pd
            from ProductDiscount pd
            where (
                    (pd.status = :activeStatus and pd.startAt <= :now and pd.endAt >= :now)
                    or
                    (pd.status = :scheduledStatus and pd.startAt > :now)
                  )
              and pd.remainingQuantity > 0
            order by pd.id desc
           """)
    Page<ProductDiscount> findActiveDiscountProducts(
            @Param("now") LocalDateTime now,
            @Param("activeStatus") DiscountStatus activeStatus,
            @Param("scheduledStatus") DiscountStatus scheduledStatus,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pd from ProductDiscount pd where pd.id = :id")
    Optional<ProductDiscount> findByIdForReservation(@Param("id") Long id);

    List<ProductDiscount> findByStatusAndEndAtBefore(DiscountStatus status, LocalDateTime endAtExclusive);

    List<ProductDiscount> findByStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqualAndRemainingQuantityGreaterThan(
            DiscountStatus status,
            LocalDateTime startInclusive,
            LocalDateTime endInclusive,
            int remainingQuantityMin
    );

    List<ProductDiscount> findByStatusAndRemainingQuantity(DiscountStatus status, int remainingQuantity);
}

