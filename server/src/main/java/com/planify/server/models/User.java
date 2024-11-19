package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private String mail;
    private char[] password;

    @OneToMany(mappedBy = "user")
    private List<UserUnavailability> userUnavailabilities = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<LessonLecturer> lessonLecturers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UEManager> ueManagers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<TAFManager> tafManagers = new ArrayList<>();

    public long getId() {
        return id;
    }

    public User(String name, String lastName, String mail, char[] password) {
        this.name = name;
        this.lastName = lastName;
        this.mail = mail;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public List<UserUnavailability> getUserUnavailabilities() {
        return userUnavailabilities;
    }

    public void setUserUnavailabilities(List<UserUnavailability> list) {
        this.userUnavailabilities = list;
    }

    public List<LessonLecturer> getLessonLecturers() {
        return lessonLecturers;
    }

    public List<UEManager> getUeManagers() {
        return ueManagers;
    }

    public List<TAFManager> getTafManagers() {
        return tafManagers;
    }

}
