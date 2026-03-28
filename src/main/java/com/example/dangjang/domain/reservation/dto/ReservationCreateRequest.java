package com.example.dangjang.domain.reservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationCreateRequest {

    @NotNull
    private Long storeId;

    @NotNull
    private LocalDate pickupDate;

    @NotNull
    private LocalTime pickupTime;

    private String requestNote;

    @Valid
    @NotEmpty
    private List<ReservationItemCreateRequest> items;

    @Getter
    @NoArgsConstructor
    public static class ReservationItemCreateRequest {
        @NotNull
        private Long productId;

        @NotNull
        private Long productDiscountId;

        @NotNull
        private Integer quantity;
    }
}
