package com.example.dangjang.domain.product.repository;

import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.entity.ProductStatus;
import com.example.dangjang.domain.store.entity.Store;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStore(Store store);

    boolean existsByStoreIdAndName(Long storeId, String name);

    Page<Product> findByNameContainingIgnoreCaseAndStatusNot(String keyword, ProductStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForReservation(@Param("id") Long id);
}

