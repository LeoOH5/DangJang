package com.example.dangjang.domain.favorite.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.favorite.dto.FavoriteCreateResponse;
import com.example.dangjang.domain.favorite.dto.FavoriteDeleteResponse;
import com.example.dangjang.domain.favorite.entity.Favorite;
import com.example.dangjang.domain.favorite.repository.FavoriteRepository;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.repository.StoreRepository;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public FavoriteCreateResponse createFavorite(String authorization, Long storeId) {
        Long userId = authService.getAuthenticatedUserId(authorization);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (favoriteRepository.existsByUserAndStore(user, store)) {
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        Favorite favorite = favoriteRepository.save(
                Favorite.builder()
                        .user(user)
                        .store(store)
                        .build()
        );

        return new FavoriteCreateResponse(favorite.getId(), store.getId());
    }

    @Transactional
    public FavoriteDeleteResponse deleteFavorite(String authorization, Long storeId) {
        Long userId = authService.getAuthenticatedUserId(authorization);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Favorite favorite = favoriteRepository.findByUserAndStore(user, store)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);

        return new FavoriteDeleteResponse(store.getId());
    }
}
