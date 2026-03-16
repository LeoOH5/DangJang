package com.example.dangjang.domain.store.dto;

import com.example.dangjang.domain.store.entity.StoreStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreUpdateRequest {

    @NotBlank(message = "name은 필수입니다.")
    @Size(max = 150, message = "name은 최대 150자입니다.")
    private String name;

    @Size(max = 10_000, message = "description이 너무 깁니다.")
    private String description;

    @Size(max = 30, message = "phone은 최대 30자입니다.")
    private String phone;

    @Size(max = 255, message = "address는 최대 255자입니다.")
    private String address;

    @NotNull(message = "openTime은 필수입니다.")
    private String openTime;

    @NotNull(message = "closeTime은 필수입니다.")
    private String closeTime;

    @NotNull(message = "status는 필수입니다.")
    private StoreStatus status;
}

