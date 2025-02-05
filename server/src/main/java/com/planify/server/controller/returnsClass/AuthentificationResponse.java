package com.planify.server.controller.returnsClass;

public class AuthentificationResponse {
    private String token;

    public AuthentificationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
