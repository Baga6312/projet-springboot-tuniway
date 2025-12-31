package com.tuniway.controller;

import com.tuniway.model.Place;
import com.tuniway.model.User;
import com.tuniway.service.UserService;
import com.tuniway.service.PlaceService;
import com.tuniway.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private JwtUtils jwtUtils;

    // Get all favorite places for current user
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFavorites(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(createError("Invalid token"));
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Set<Place> favorites = user.getFavoritePlaces();
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createError("Error fetching favorites: " + e.getMessage()));
        }
    }

    // Add place to favorites
    @PostMapping("/{placeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addFavorite(@PathVariable Long placeId, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(createError("Invalid token"));
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Place place = placeService.getPlaceById(placeId)
                    .orElseThrow(() -> new RuntimeException("Place not found"));

            user.getFavoritePlaces().add(place);
            userService.updateUser(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Place added to favorites",
                    "placeId", placeId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createError("Error adding favorite: " + e.getMessage()));
        }
    }

    // Remove place from favorites
    @DeleteMapping("/{placeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeFavorite(@PathVariable Long placeId, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(createError("Invalid token"));
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Place place = placeService.getPlaceById(placeId)
                    .orElseThrow(() -> new RuntimeException("Place not found"));

            user.getFavoritePlaces().remove(place);
            userService.updateUser(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Place removed from favorites",
                    "placeId", placeId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createError("Error removing favorite: " + e.getMessage()));
        }
    }

    // Check if place is favorited
    @GetMapping("/check/{placeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> checkFavorite(@PathVariable Long placeId, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(createError("Invalid token"));
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean isFavorite = user.getFavoritePlaces().stream()
                    .anyMatch(p -> p.getId().equals(placeId));

            return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createError("Error checking favorite: " + e.getMessage()));
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
}
