package com.example.dangjang.domain.market.repository;

import com.example.dangjang.domain.market.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {
    boolean existsByNameAndAddress(String name, String address);

    Optional<Market> findByNameAndAddress(String name, String address);
}

