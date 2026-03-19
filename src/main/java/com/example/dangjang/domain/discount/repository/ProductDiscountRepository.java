package com.example.dangjang.domain.discount.repository;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    List<ProductDiscount> findByProductAndStatus(Product product, DiscountStatus status);

    List<ProductDiscount> findByProduct(Product product);
}

