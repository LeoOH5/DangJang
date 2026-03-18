package com.example.dangjang.domain.product.repository;

import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStore(Store store);

    boolean existsByStoreIdAndName(Long storeId, String name);
}

