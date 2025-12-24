package com.tuniway.controller;

import com.tuniway.model.Admin;
import com.tuniway.model.Client;
import com.tuniway.model.Guide;
import com.tuniway.model.User;
import com.tuniway.model.enums.RoleType;
import com.tuniway.jwt.JwtUtils;
import com.tuniway.service.UserDetailsImpl;
import com.tuniway.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // DTO Classes
    public static class LoginRequest {
        @Valid
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class SignupRequest {
        @Valid
        private String username;
        private String email;
        private String password;
        private String role; // "CLIENT", "GUIDE", "ADMIN"

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }


    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String username;
        private String email;
        private String role;
        private String profilePicture;  // ✅ ADD THIS

        public JwtResponse(String accessToken, Long id, String username, String email, String role, String profilePicture) {  // ✅ ADD profilePicture parameter
            this.token = accessToken;
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
            this.profilePicture = profilePicture;  // ✅ ADD THIS
        }

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getProfilePicture() { return profilePicture; }  // ✅ ADD THIS
        public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }  // ✅ ADD THIS
    }


        
        
        
        
        
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("===== SIGNIN CALLED =====");
        System.out.println("Identifier: " + loginRequest.getUsername());
        System.out.println("Password received: " + (loginRequest.getPassword() != null ? "YES" : "NO"));

        try {
            // Try to find user by username or email
            String identifier = loginRequest.getUsername();
            User user = null;

            // Check if identifier is an email
            if (identifier != null && identifier.contains("@")) {
                System.out.println("Looking up by EMAIL: " + identifier);
                user = userService.findByEmail(identifier).orElse(null);
            } else {
                System.out.println("Looking up by USERNAME: " + identifier);
                user = userService.findByUsername(identifier).orElse(null);
            }

            if (user == null) {
                System.out.println("User NOT FOUND");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid username or password"));
            }

            System.out.println("User FOUND: " + user.getUsername());

            // Authenticate with the actual username
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            System.out.println("Authentication SUCCESS! Token generated.");

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getAuthorities().iterator().next().getAuthority(),
                    user.getProfilePicture()  // ✅ ADD THIS - pass the profilePicture from user
            ));
        
        } catch (Exception e) {
            System.out.println("Authentication FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Validate input
        if (signUpRequest.getUsername() == null || signUpRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is required"));
        }

        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is required"));
        }

        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Password must be at least 6 characters"));
        }

        // Check if username exists
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email exists
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user based on role
        User newUser;
        String roleStr = signUpRequest.getRole() != null ?
                signUpRequest.getRole().toUpperCase() : "CLIENT";

        switch (roleStr) {
            case "GUIDE":
                newUser = new Guide();
                break;
            case "ADMIN":
                newUser = new Admin();
                break;
            default:
                newUser = new Client();
                break;
        }

        newUser.setUsername(signUpRequest.getUsername());
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        User savedUser = userService.createUser(newUser);

        // Generate JWT token for the new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signUpRequest.getUsername(),
                        signUpRequest.getPassword()
                )
        );

        String jwt = jwtUtils.generateJwtToken(authentication);

        // Return same format as signin (with token!)
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getProfilePicture()  // ✅ ADD THIS
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "No token provided"));
            }

            // Extract token from "Bearer <token>"
            String token = authHeader.substring(7);

            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid token"));
            }

            // Get username from token
            String username = jwtUtils.getUserNameFromJwtToken(token);

            // Get user details
            Optional<User> userOpt = userService.findByUsername(username);

            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();

            // Return user data with same format as signin

            return ResponseEntity.ok(new JwtResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getProfilePicture()  // ✅ ADD THIS
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token validation failed: " + e.getMessage()));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}