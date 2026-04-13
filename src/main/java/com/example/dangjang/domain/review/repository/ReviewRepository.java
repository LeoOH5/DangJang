package com.example.dangjang.domain.review.repository;

import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservation(Reservation reservation);

    long countByStore_Id(Long storeId);

    Page<Review> findByStore_IdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    Page<Review> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("select coalesce(avg(r.rating), 0.0) from Review r where r.store.id = :storeId")
    Double averageRatingByStoreId(@Param("storeId") Long storeId);
}

