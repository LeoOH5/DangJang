package com.example.dangjang.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),

    // Auth
    MISSING_ADMIN_KEY(HttpStatus.UNAUTHORIZED, "AUTH_001", "X-Admin-Key 헤더가 필요합니다."),
    INVALID_ADMIN_KEY(HttpStatus.FORBIDDEN, "AUTH_002", "유효하지 않은 관리자 키입니다."),
    ADMIN_ONLY(HttpStatus.FORBIDDEN, "AUTH_003", "관리자 권한이 필요한 요청입니다."),

    // Market
    MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET_001", "존재하지 않는 시장입니다."),
    MARKET_INACTIVE(HttpStatus.BAD_REQUEST, "MARKET_002", "비활성화된 시장입니다."),

    // Store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_001", "존재하지 않는 매장입니다."),
    STORE_INACTIVE(HttpStatus.BAD_REQUEST, "STORE_002", "비활성화된 매장입니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "존재하지 않는 상품입니다."),
    PRODUCT_INACTIVE(HttpStatus.BAD_REQUEST, "PRODUCT_002", "비활성화된 상품입니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "PRODUCT_003", "재고 수량은 0 이상이어야 합니다."),

    // Discount
    DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "DISCOUNT_001", "존재하지 않는 할인 정보입니다."),
    INVALID_DISCOUNT_PERIOD(HttpStatus.BAD_REQUEST, "DISCOUNT_002", "할인 시작 시간은 종료 시간보다 빨라야 합니다."),
    INVALID_DISCOUNT_VALUE(HttpStatus.BAD_REQUEST, "DISCOUNT_003", "유효하지 않은 할인 값입니다."),
    INVALID_REMAINING_QUANTITY(HttpStatus.BAD_REQUEST, "DISCOUNT_004", "남은 할인 수량은 0 이상이어야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
