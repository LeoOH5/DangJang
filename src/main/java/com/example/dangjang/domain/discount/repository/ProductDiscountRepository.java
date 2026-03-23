package com.example.dangjang.domain.discount.repository;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDateTime;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    List<ProductDiscount> findByProductAndStatus(Product product, DiscountStatus status);

    List<ProductDiscount> findByProduct(Product product);

    @Query("""
            select pd
            from ProductDiscount pd
            where pd.startAt <= :now
              and pd.endAt >= :now
              and pd.remainingQuantity > 0
            order by pd.id desc
           """)
    Page<ProductDiscount> findActiveDiscountProducts(
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}

