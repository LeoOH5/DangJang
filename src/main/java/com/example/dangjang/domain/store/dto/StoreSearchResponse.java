package com.example.dangjang.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreSearchResponse {

    private final List<StoreSearchSummaryResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}
