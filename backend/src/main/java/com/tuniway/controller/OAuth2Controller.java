package com.tuniway.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@CrossOrigin(origins = "*")
public class OAuth2Controller {

    @GetMapping("/success")
    public Map<String, String> oauth2Success(
            @RequestParam String token,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam Long id
    ) {
        return Map.of(
                "token", token,
                "username", username,
                "email", email,
                "role", role,
                "id", String.valueOf(id),
                "message", "OAuth2 authentication successful"
        );
    }
}