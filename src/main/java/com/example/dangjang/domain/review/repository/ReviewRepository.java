package com.example.dangjang.domain.review.repository;

import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservation(Reservation reservation);
}

