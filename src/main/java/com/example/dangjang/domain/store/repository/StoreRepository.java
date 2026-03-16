package com.example.dangjang.domain.store.repository;

import com.example.dangjang.domain.market.entity.Market;
import com.example.dangjang.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByMarket(Market market);
}

