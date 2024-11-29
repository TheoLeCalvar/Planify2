package com.planify.server.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserUnavailability> userUnavailabilities = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<LessonLecturer> lessonLecturers = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UEManager> ueManagers = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
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

    public String toString() {
        return "User " + Long.toString(this.id) + "\n Name: " + name + " " + lastName + "\n mail: " + "" + mail;
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

    public void setLessonLecturers(List<LessonLecturer> lessonLecturers) {
        this.lessonLecturers = lessonLecturers;
    }

    public List<UEManager> getUeManagers() {
        return ueManagers;
    }

    public void setUeManagers(List<UEManager> ueManagers) {
        this.ueManagers = ueManagers;
    }

    public List<TAFManager> getTafManagers() {
        return tafManagers;
    }

    public void setTafManagers(List<TAFManager> tafManagers) {
        this.tafManagers = tafManagers;
    }

}
