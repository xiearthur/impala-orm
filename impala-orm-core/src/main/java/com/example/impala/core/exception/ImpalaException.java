package com.example.impala.core.exception;

public class ImpalaException extends RuntimeException {
    private String errorCode;
    private String sqlState;

    public ImpalaException(String message) {
        super(message);
    }

    public ImpalaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImpalaException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ImpalaException(String message, String errorCode, String sqlState) {
        super(message);
        this.errorCode = errorCode;
        this.sqlState = sqlState;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getSqlState() {
        return sqlState;
    }
}