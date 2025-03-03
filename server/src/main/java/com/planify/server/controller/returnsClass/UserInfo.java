package com.planify.server.controller.returnsClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfo {
  private Map<String, Boolean> profiles = new HashMap<>();
  private Long id;
  private String firstName;
  private String lastName;
  private String mail;

  public UserInfo(List<String> roles, Long id, String firstName, String lastName, String mail) {
      this.profiles.put("admin", roles.contains("ROLE_ADMIN"));
      this.profiles.put("lecturer", roles.contains("ROLE_LESSONLECTURER"));
      this.profiles.put("ue_manager", roles.contains("ROLE_UEMANAGER"));
      this.profiles.put("taf_manager", roles.contains("ROLE_TAFMANAGER")); 
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
      this.mail = mail;
  }

  public Map<String, Boolean> getProfiles() {
      return profiles;
  }

  public Long getId() {
      return id;
  }

  public String getFirstName() {
      return firstName;
  }

  public String getLastName() {
      return lastName;
  }

  public String getMail() {
      return mail;
  }
}