package com.example.dangjang.domain.favorite.repository;

import com.example.dangjang.domain.favorite.entity.Favorite;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndStore(User user, Store store);

    Optional<Favorite> findByUserAndStore(User user, Store store);

    Page<Favorite> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    @Query("select f from Favorite f where f.store.id = :storeId")
    List<Favorite> findAllWithUserByStoreId(@Param("storeId") Long storeId);
}

