package com.planify.server.controller.returnsClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthentificationResponse {

    private String token;

    private Map<String, Boolean> profiles = new HashMap<>();

    public AuthentificationResponse(String token, List<String> roles) {
        this.token = token;
        this.profiles.put("admin", roles.contains("ROLE_ADMIN"));
        this.profiles.put("lecturer", roles.contains("ROLE_LESSONLECTURER"));
        this.profiles.put("ue_manager", roles.contains("ROLE_UEMANAGER"));
        this.profiles.put("taf_manager", roles.contains("ROLE_TAFMANAGER"));
    }

    public String getToken() {
        return token;
    }

    public Map<String, Boolean> getProfiles() {
        return profiles;
    }
}
