package com.example.dangjang.domain.favorite.repository;

import com.example.dangjang.domain.favorite.entity.Favorite;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndStore(User user, Store store);
}

