package com.example.dangjang.domain.reservation.repository;

import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"store"})
    Page<Reservation> findByUserOrderByIdDesc(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Reservation> findByUserAndStatusOrderByIdDesc(User user, String status, Pageable pageable);
}

