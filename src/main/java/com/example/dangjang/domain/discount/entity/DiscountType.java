package com.example.dangjang.domain.discount.entity;

public enum DiscountType {
    AMOUNT,
    PERCENT,

    FIXED_AMOUNT,
    PERCENTAGE;

    public boolean isPercent() {
        return this == PERCENT || this == PERCENTAGE;
    }

    public boolean isAmount() {
        return this == AMOUNT || this == FIXED_AMOUNT;
    }

    public DiscountType normalize() {
        if (isPercent()) {
            return PERCENT;
        }
        if (isAmount()) {
            return AMOUNT;
        }
        return this;
    }
}
