package com.tuniway.controller;

import com.tuniway.dto.UpdateProfileRequest;
import com.tuniway.model.* ;
import com.tuniway.service.* ;
import com.tuniway.util.validation.*;
import com.tuniway.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private ReviewCheckHandler reviewCheckHandler;

    @Autowired
    private ClientReservationCheckHandler clientReservationCheckHandler;

    @Autowired
    private GuideReservationCheckHandler guideReservationCheckHandler;

    @Autowired
    private GuideTourCheckHandler guideTourCheckHandler ;

    @Autowired
    private ClientTourCheckHandler clientTourCheckHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TourPersonnaliseService tourPersonnaliseService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody Client client) {
        if (userService.existsByUsername(client.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        if (userService.existsByEmail(client.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        Client savedClient = (Client) userService.createUser(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    @PostMapping("/register/guide")
    public ResponseEntity<?> registerGuide(@RequestBody Guide guide) {
        if (userService.existsByUsername(guide.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        if (userService.existsByEmail(guide.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        Guide savedGuide = (Guide) userService.createUser(guide);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGuide);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        if (userService.existsByUsername(admin.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        if (userService.existsByEmail(admin.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        Admin savedAdmin = (Admin) userService.createUser(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.getUserByUsername(loginRequest.getUsername());

        if (user.isPresent()) {
            if (user.get().getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(user.get());
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User userDetails) {
        Optional<User> existingUser = userService.getUserById(id);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setProfilePicture(userDetails.getProfilePicture());

            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        }

        return ResponseEntity.notFound().build();
    }

    // ========== NEW: PROFILE UPDATE ENDPOINT ==========
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request, HttpServletRequest httpRequest) {
        try {
            // Extract JWT token from Authorization header
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(createError("No token provided"));
            }

            // Validate token and get username
            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(createError("Invalid token"));
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);

            // Find user
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update fields (only if provided)
            if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                user.setUsername(request.getUsername());
            }
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                user.setEmail(request.getEmail());
            }
            if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
                // Store the base64 image directly in database
                user.setProfilePicture(request.getProfilePicture());
            }

            // Save updated user
            User updatedUser = userService.updateUser(user);

            // Return updated user data
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("username", updatedUser.getUsername());
            response.put("email", updatedUser.getEmail());
            response.put("role", updatedUser.getRole().toString());
            response.put("profilePicture", updatedUser.getProfilePicture());
            response.put("message", "Profile updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createError("Error updating profile: " + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProfile(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(createError("No token provided"));
            }

            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(createError("Invalid token"));
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().toString());
            response.put("profilePicture", user.getProfilePicture());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createError("Error fetching profile: " + e.getMessage()));
        }
    }



    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userService.getUserById(id);

            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().toString());
            response.put("profilePicture", user.getProfilePicture());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createError("Error fetching profile: " + e.getMessage()));
        }
    }





    private String extractTokenFromHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private Map<String, String> createError(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
    // ========== END NEW ENDPOINTS ==========

    @Autowired
    private DeletionValidationChainService deletionValidationChainService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Execute the validation chain through the service
        DeletionResult result = deletionValidationChainService.validateDeletion(user.get());

        if (!result.isCanDelete()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "success", false,
                            "message", result.getMessage(),
                            "userId", id
                    ));
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/can-delete")
    public ResponseEntity<?> checkDeletionPossibility(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        DeletionResult result = deletionValidationChainService.validateDeletion(user.get());

        return ResponseEntity.ok(Map.of(
                "canDelete", result.isCanDelete(),
                "message", result.getMessage(),
                "userId", id,
                "username", user.get().getUsername()
        ));
    }

    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }
}