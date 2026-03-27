package com.example.dangjang.domain.reservation.entity;

import com.example.dangjang.common.entity.BaseTimeEntity;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "pickup_time", nullable = false)
    private LocalTime pickupTime;

    @Column(name = "request_note", length = 500)
    private String requestNote;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> items = new ArrayList<>();

    @Builder
    public Reservation(User user, Store store, LocalDate pickupDate, LocalTime pickupTime, String requestNote, String status) {
        this.user = user;
        this.store = store;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.requestNote = requestNote;
        this.status = status;
    }

    public void addItem(ReservationItem item) {
        this.items.add(item);
    }
}

