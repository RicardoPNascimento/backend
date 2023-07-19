package com.simbioff.simbioff.enums;

public enum ResponseStatus {
    ACCEPTED(200),
    NOT_FOUND(404),
    BAD_REQUEST(400);

    private final int statusCode;

    ResponseStatus(int statusCode){
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
