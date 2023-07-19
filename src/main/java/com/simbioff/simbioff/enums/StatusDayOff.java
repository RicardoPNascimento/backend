package com.simbioff.simbioff.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Optional;

public enum StatusDayOff {
    APPROVED,
    REJECTED,
    PENDING;

    @JsonCreator
    public static StatusDayOff getStatusDayOffFromString(String statusDayOff) {
        return Optional.ofNullable(statusDayOff)
                .map(value -> StatusDayOff.valueOf(value.toUpperCase()))
                .orElse(null);
    }
}
