package com.example.dangjang.domain.reservation.repository;

import com.example.dangjang.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

