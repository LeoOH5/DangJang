package com.example.dangjang.domain.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteSummaryResponse {

    private final Long favoriteId;
    private final Long storeId;
    private final String storeName;
    private final String marketName;
    private final boolean isFavorite;
}
