package com.example.dangjang.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, "CREATED", message, data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공적으로 처리되었습니다.", null);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, "SUCCESS", message, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
