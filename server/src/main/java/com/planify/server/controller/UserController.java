package com.planify.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.planify.server.controller.returnsClass.UserShort;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.TAF;
import com.planify.server.models.User;
import com.planify.server.service.LessonLecturerService;
import com.planify.server.service.TAFManagerService;
import com.planify.server.service.TAFService;
import com.planify.server.service.UEManagerService;
import com.planify.server.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TAFManagerService tafManagerService;

    @Autowired
    private UEManagerService ueManagerService;

    @Autowired
    private LessonLecturerService lessonLecturerService;

    @Autowired
    private TAFService tafService;

    // Get the list of the users
    @RequestMapping(value = "users", method = RequestMethod.GET)
    public ResponseEntity<?> getTAFById(@RequestParam("tafId") Long tafId) {
        Optional<TAF> taf = tafService.findById(tafId);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        List<User> users = userService.findAll();
        List<UserShort> answers = new ArrayList<>();
        for (User user : users) {
            List<TAF> tafs = (user.getLessonLecturers()).stream().map(LessonLecturer::getTAF).filter(x -> x.equals(taf))
                    .collect(Collectors.toList());
            if (!tafs.isEmpty()) {
                answers.add(new UserShort(user.getId(), user.getFullName(), true));
            } else {
                answers.add(new UserShort(user.getId(), user.getFullName(), false));
            }
        }
        return ResponseEntity.ok(answers);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserShort userRequest) {
        Optional<User> user = userService.findById(userRequest.getId());

        if (user.isPresent()) {
            return ResponseEntity.status(209).body("User already exists");
        }
        userService.addUser(userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(),
                "not implemented".toCharArray());
        return ResponseEntity.ok("User created !");
    }

}