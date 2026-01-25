package com.utown.exception;

public class ConflictException extends RuntimeException {

    private String errorCode;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}