package com.example.dangjang.domain.store.repository;

import com.example.dangjang.domain.market.entity.Market;
import com.example.dangjang.domain.market.entity.MarketStatus;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.entity.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByMarket(Market market);

    @Query("""
            select s from Store s
            join s.market m
            where s.status <> :inactiveStoreStatus
            and m.status = :activeMarketStatus
            and (
                :keyword is null or :keyword = ''
                or lower(s.name) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(s.description, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(s.address, '')) like lower(concat('%', :keyword, '%'))
            )
            and (:marketId is null or m.id = :marketId)
            and (
                :city is null or :city = ''
                or lower(coalesce(m.city, '')) like lower(concat('%', :city, '%'))
            )
            and (
                :district is null or :district = ''
                or lower(coalesce(m.district, '')) like lower(concat('%', :district, '%'))
            )
            """)
    Page<Store> searchStores(
            @Param("keyword") String keyword,
            @Param("marketId") Long marketId,
            @Param("city") String city,
            @Param("district") String district,
            @Param("inactiveStoreStatus") StoreStatus inactiveStoreStatus,
            @Param("activeMarketStatus") MarketStatus activeMarketStatus,
            Pageable pageable
    );
}

