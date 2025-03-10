package com.planify.server.controller.returnsClass;

import java.util.List;

public class UELecturerShort {

  private Long id;

  private String title;

  private String description;

  private List<UserBrief> managers;

  private List<LessonShortLecturer> lessons;

  public UELecturerShort(Long id, String title, String description, List<UserBrief> managers, List<LessonShortLecturer> lessons) {
      this.id = id;
      this.title = title;
      this.description = description;
      this.managers = managers;
      this.lessons = lessons;
  }

  public Long getId() {
      return this.id;
  }

  public void setId(Long id) {
      this.id = id;
  }

  public String getTitle() {
      return this.title;
  }

  public void setTitle(String title) {
      this.title = title;
  }

  public String getDescription() {
      return this.description;
  }

  public void setDescription(String description) {
      this.description = description;
  }

  public List<UserBrief> getManagers() {
      return managers;
  }

  public void setManagers(List<UserBrief> managers) {
      this.managers = managers;
  }

  public List<LessonShortLecturer> getLessons() {
      return lessons;
  }

  public void setLessons(List<LessonShortLecturer> lessons) {
      this.lessons = lessons;
  }


  
}
