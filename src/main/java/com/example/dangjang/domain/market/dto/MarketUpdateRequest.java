package com.example.dangjang.domain.market.dto;

import com.example.dangjang.domain.market.entity.MarketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MarketUpdateRequest {

    @NotBlank(message = "name은 필수입니다.")
    @Size(max = 150, message = "name은 최대 150자입니다.")
    private String name;

    @Size(max = 10_000, message = "description이 너무 깁니다.")
    private String description;

    @NotBlank(message = "address는 필수입니다.")
    @Size(max = 255, message = "address는 최대 255자입니다.")
    private String address;

    @Size(max = 100, message = "city는 최대 100자입니다.")
    private String city;

    @Size(max = 100, message = "district는 최대 100자입니다.")
    private String district;

    @NotNull(message = "status는 필수입니다.")
    private MarketStatus status;
}

