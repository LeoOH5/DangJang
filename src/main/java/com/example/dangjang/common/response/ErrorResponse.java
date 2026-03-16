package com.example.dangjang.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private final boolean success;
    private final String code;
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;
    private final List<FieldErrorDetail> errors;

    @Builder
    public static class FieldErrorDetail {
        private final String field;
        private final Object rejectedValue;
        private final String reason;
    }
}
