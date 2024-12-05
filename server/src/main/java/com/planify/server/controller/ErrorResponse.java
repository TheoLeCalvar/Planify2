package com.planify.server.controller;

public class ErrorResponse {
    private String error;
    private int code;

    public ErrorResponse(String error, int code) {
        this.error = error;
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public int getCode() {
        return code;
    }
}
