package com.example.dangjang.domain.favorite.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.favorite.dto.FavoriteCreateResponse;
import com.example.dangjang.domain.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/stores/{storeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FavoriteCreateResponse> createFavorite(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long storeId
    ) {
        FavoriteCreateResponse response = favoriteService.createFavorite(authorization, storeId);
        return ApiResponse.ok("즐겨찾기에 추가되었습니다.", response);
    }
}
