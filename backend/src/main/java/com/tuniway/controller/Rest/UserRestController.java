package com.tuniway.controller.Rest;


import com.tuniway.model.*;
import com.tuniway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rest/users")
@CrossOrigin(origins = "*")
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/client")
    public ResponseEntity<?> ajouterClient(@RequestBody Client client) {
        if (userService.existsByUsername(client.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        }
        if (userService.existsByEmail(client.getEmail())) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }
        Client savedClient = (Client) userService.createUser(client);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @PostMapping("/guide")
    public ResponseEntity<?> ajouterGuide(@RequestBody Guide guide) {
        if (userService.existsByUsername(guide.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        }
        if (userService.existsByEmail(guide.getEmail())) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }
        Guide savedGuide = (Guide) userService.createUser(guide);
        return new ResponseEntity<>(savedGuide, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> existingUser = userService.getUserById(id);

        if (!existingUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = existingUser.get();
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setProfilePicture(userDetails.getProfilePicture());

        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.getUserByUsername(loginRequest.getUsername());

        if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}