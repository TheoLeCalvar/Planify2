package com.planify.server.controller.returnsClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthentificationResponse {

    private String token;

    private UserInfo userInfo;

    public AuthentificationResponse(String token, List<String> roles, Long id, String firstName, String lastName, String mail) {
        this.token = token;
        this.userInfo = new UserInfo(roles, id, firstName, lastName, mail);
    }

    public String getToken() {
        return token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
