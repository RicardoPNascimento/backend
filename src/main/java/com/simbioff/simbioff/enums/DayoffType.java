package com.simbioff.simbioff.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Optional;

public enum DayoffType {
    DAYOFF,
    HALF_DAYOFF;

    @JsonCreator
    public static DayoffType getDayoffTypeFromString(String dayoffType) {
        return Optional.ofNullable(dayoffType)
                .map(value -> DayoffType.valueOf(value.toUpperCase()))
                .orElse(null);
    }
}
