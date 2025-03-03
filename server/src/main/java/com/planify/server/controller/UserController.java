package com.planify.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.planify.server.controller.returnsClass.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.planify.server.authentification.JwtUtil;
import com.planify.server.models.LessonLecturer;
import com.planify.server.models.TAF;
import com.planify.server.models.User;
import com.planify.server.repo.UserRepository;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get the list of the users
    @Operation(summary = "Get the list of the users and precising if they already work for this TAF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of the userd",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserShort.class)))),
            @ApiResponse(responseCode = "400", description = "No TAF with this id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "users", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers(@RequestParam(value="tafId", required = false) Long tafId) {
        List<User> users = userService.findAll();
        List<UserShort> answers = new ArrayList<>();
        if (tafId == null) {
            for (User user : users) {
                answers.add(new UserShort(user.getId(), user.getFullName(), false));
            }
            return ResponseEntity.ok(answers);
        }
        Optional<TAF> taf = tafService.findById(tafId);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        for (User user : users) {
            List<TAF> tafs = (user.getLessonLecturers()).stream().map(LessonLecturer::getTAF).filter(x -> x.equals(taf.get()))
                    .toList();
            if (!tafs.isEmpty()) {
                answers.add(new UserShort(user.getId(), user.getFullName(), true));
            } else {
                answers.add(new UserShort(user.getId(), user.getFullName(), false));
            }
        }
        return ResponseEntity.ok(answers);
    }

    @Operation(summary = "Create a new user")
    @PostMapping(value = "users", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createUser(@RequestBody UserShort userRequest) {
        List<User> users = userService.findAll();
        boolean exists = users.stream().anyMatch(user -> user.getName().equals(userRequest.getFirstName())
                && user.getLastName().equals(userRequest.getLastName()));

        if (exists) {
            return ResponseEntity.status(209).body("User already exists");
        }
        System.out.println(userRequest.getFirstName());
        System.out.println(userRequest.getLastName());
        User u = userService.addUser(userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(),
                "not implemented");
        return ResponseEntity.ok("User created !");
    }

    @Operation(summary = "Register the new user")
    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Check if username already exists
        if (userService.findByMail(user.getMail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "To log in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AuthentificationResponse.class)))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthentificationRequest authRequest) {
        try {
            // Authenticate user credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getMail(), authRequest.getPassword()));

            // Retrieve UserDetails from UserRepository
            Optional<User> userOptional = userService.findByMail(authRequest.getMail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    userOptional.get().getMail(), userOptional.get().getPassword(), new ArrayList<>());

            // Generate JWT token
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            // Get roles
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Return token in response
            return ResponseEntity.ok(new AuthentificationResponse(jwt, roles));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @Operation(summary = "Change an user's password")
    @PutMapping("/user/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOptional = userService.findByMail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect");
        }

        // Hash and save new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }

}