package com.tuniway.controller;

import com.tuniway.model.* ;
import com.tuniway.service.* ;
import com.tuniway.util.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        reviewCheckHandler
                .setNext(clientReservationCheckHandler)
                .setNext(clientTourCheckHandler)
                .setNext(guideReservationCheckHandler)
                .setNext(guideTourCheckHandler);

        // Execute the chain
        DeletionResult result = reviewCheckHandler.canDelete(user.get());

        if (!result.isCanDelete()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result.getMessage());
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
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