package com.example.dangjang.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    INVALID_SEARCH_CONDITION(HttpStatus.BAD_REQUEST, "INVALID_SEARCH_CONDITION", "검색 조건이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),

    // Auth
    MISSING_ADMIN_KEY(HttpStatus.UNAUTHORIZED, "AUTH_001", "X-Admin-Key 헤더가 필요합니다."),
    INVALID_ADMIN_KEY(HttpStatus.FORBIDDEN, "AUTH_002", "유효하지 않은 관리자 키입니다."),
    ADMIN_ONLY(HttpStatus.FORBIDDEN, "AUTH_003", "관리자 권한이 필요한 요청입니다."),

    // User
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "REQUIRED_FIELD_MISSING", "필수 입력 값이 누락되었습니다."),
    AUTH_INVALID_INPUT(HttpStatus.BAD_REQUEST, "AUTH_INVALID_INPUT", "입력값이 올바르지 않습니다."),
    AUTH_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "AUTH_EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."),
    AUTH_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH_PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다."),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "로그인이 필요합니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_EXPIRED_TOKEN", "만료된 토큰입니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_TOKEN", "유효하지 않은 토큰입니다."),

    // Market
    MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET_001", "존재하지 않는 시장입니다."),
    MARKET_INACTIVE(HttpStatus.BAD_REQUEST, "MARKET_002", "비활성화된 시장입니다."),
    MARKET_ALREADY_EXISTS(HttpStatus.CONFLICT, "MARKET_ALREADY_EXISTS", "이미 등록된 시장입니다."),

    // Store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND", "존재하지 않는 매장입니다."),
    STORE_INACTIVE(HttpStatus.BAD_REQUEST, "STORE_002", "비활성화된 매장입니다."),
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "FAVORITE_ALREADY_EXISTS", "이미 즐겨찾기에 등록된 매장입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE_NOT_FOUND", "즐겨찾기에 등록되지 않은 매장입니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "존재하지 않는 상품입니다."),
    PRODUCT_INACTIVE(HttpStatus.BAD_REQUEST, "PRODUCT_002", "비활성화된 상품입니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "PRODUCT_003", "재고 수량은 0 이상이어야 합니다."),
    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "PRODUCT_ALREADY_EXISTS", "이미 등록된 상품입니다."),
    RESERVATION_QUANTITY_EXCEEDED(HttpStatus.BAD_REQUEST, "RESERVATION_QUANTITY_EXCEEDED", "예약 가능 수량을 초과했습니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "OUT_OF_STOCK", "재고가 부족합니다."),
    DISCOUNT_QUANTITY_EXCEEDED(HttpStatus.BAD_REQUEST, "DISCOUNT_QUANTITY_EXCEEDED", "할인 잔여 수량이 부족합니다."),

    // Discount
    DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "DISCOUNT_001", "존재하지 않는 할인 정보입니다."),
    INVALID_DISCOUNT_PERIOD(HttpStatus.BAD_REQUEST, "DISCOUNT_002", "할인 시작 시간은 종료 시간보다 빨라야 합니다."),
    INVALID_DISCOUNT_TIME(HttpStatus.BAD_REQUEST, "INVALID_DISCOUNT_TIME", "할인 시작 시간과 종료 시간이 올바르지 않습니다."),
    INVALID_DISCOUNT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_DISCOUNT_VALUE", "할인 값이 올바르지 않습니다."),
    INVALID_REMAINING_QUANTITY(HttpStatus.BAD_REQUEST, "INVALID_REMAINING_QUANTITY", "남은 할인 수량은 0 이상이어야 합니다."),
    PRODUCT_DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_DISCOUNT_NOT_FOUND", "존재하지 않는 상품 할인 정보입니다."),
    DISCOUNT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "DISCOUNT_NOT_AVAILABLE", "예약 가능한 할인 상품이 아닙니다."),
    DISCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "DISCOUNT_ALREADY_EXISTS", "이미 등록된 할인 정보입니다."),
    ACTIVE_DISCOUNT_CONFLICT(HttpStatus.CONFLICT, "ACTIVE_DISCOUNT_CONFLICT", "이미 진행 중인 할인 정보가 존재합니다."),

    // Reservation
    RESERVATION_INVALID_STATUS(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "잘못된 예약 상태 값입니다."),
    RESERVATION_APPROVE_INVALID_STATUS(HttpStatus.BAD_REQUEST, "RESERVATION_INVALID_STATUS", "현재 상태에서는 승인할 수 없습니다."),
    RESERVATION_REJECT_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "REQUIRED_FIELD_MISSING", "거절 사유가 필요합니다."),
    RESERVATION_REJECT_INVALID_STATUS(HttpStatus.BAD_REQUEST, "RESERVATION_INVALID_STATUS", "현재 상태에서는 거절할 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "해당 예약에 접근할 권한이 없습니다."),
    RESERVATION_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "RESERVATION_CANNOT_CANCEL", "취소할 수 없는 예약 상태입니다."),
    RESERVATION_ALREADY_CANCELED(HttpStatus.CONFLICT, "RESERVATION_ALREADY_CANCELED", "이미 취소된 예약입니다."),
    STORE_OWNER_FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "매장 주인만 접근할 수 있습니다."),
    STORE_RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "해당 매장의 예약을 조회할 권한이 없습니다."),
    CONCURRENT_RESERVATION_CONFLICT(HttpStatus.CONFLICT, "CONCURRENT_RESERVATION_CONFLICT", "동시에 예약이 처리되어 재시도가 필요합니다."),

    // Review
    REVIEW_INVALID_RATING(HttpStatus.BAD_REQUEST, "REV_005", "평점은 1점부터 5점까지 입력 가능합니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "REVIEW_ALREADY_EXISTS", "이미 해당 예약에 대한 리뷰를 작성했습니다."),
    REVIEW_NOT_ALLOWED(HttpStatus.FORBIDDEN, "REVIEW_NOT_ALLOWED", "픽업 완료한 예약에 대해서만 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "존재하지 않는 리뷰입니다."),
    REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "본인의 리뷰만 수정 또는 삭제할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
