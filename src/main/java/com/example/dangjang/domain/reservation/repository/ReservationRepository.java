package com.example.dangjang.domain.reservation.repository;

import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"store"})
    Page<Reservation> findByUserOrderByIdDesc(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Reservation> findByUserAndStatusOrderByIdDesc(User user, String status, Pageable pageable);

    @Query("""
            select distinct r from Reservation r
            left join fetch r.user
            left join fetch r.store
            left join fetch r.items i
            left join fetch i.product
            where r.id = :id
            """)
    Optional<Reservation> findDetailById(@Param("id") Long id);
}

