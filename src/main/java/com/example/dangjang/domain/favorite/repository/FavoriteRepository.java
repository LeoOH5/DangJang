package com.example.dangjang.domain.favorite.repository;

import com.example.dangjang.domain.favorite.entity.Favorite;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndStore(User user, Store store);

    Optional<Favorite> findByUserAndStore(User user, Store store);

    Page<Favorite> findByUser(User user, Pageable pageable);
}

