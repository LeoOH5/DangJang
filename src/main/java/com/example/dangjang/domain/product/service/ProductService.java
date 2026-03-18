package com.example.dangjang.domain.product.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.product.dto.ProductCreateRequest;
import com.example.dangjang.domain.product.dto.ProductCreateResponse;
import com.example.dangjang.domain.product.dto.ProductUpdateRequest;
import com.example.dangjang.domain.product.dto.ProductUpdateResponse;
import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.entity.ProductStatus;
import com.example.dangjang.domain.product.repository.ProductRepository;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.entity.StoreStatus;
import com.example.dangjang.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ProductCreateResponse createProduct(ProductCreateRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (store.getStatus() == StoreStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.STORE_INACTIVE);
        }

        boolean exists = productRepository.existsByStoreIdAndName(store.getId(), request.getName());
        if (exists) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Product product = Product.builder()
                .store(store)
                .name(request.getName())
                .description(request.getDescription())
                .originalPrice(request.getOriginalPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .build();

        Product saved = productRepository.save(product);
        return new ProductCreateResponse(saved.getId());
    }

    @Transactional
    public ProductUpdateResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.PRODUCT_INACTIVE);
        }

        product.update(
                request.getName(),
                request.getDescription(),
                request.getOriginalPrice(),
                request.getStockQuantity(),
                request.getImageUrl(),
                request.getStatus()
        );

        return new ProductUpdateResponse(product.getId());
    }
}
