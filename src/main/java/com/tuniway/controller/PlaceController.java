package com.tuniway.controller;

import com.tuniway.model.Place;
import com.tuniway.model.enums.PlaceCategory;
import com.tuniway.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    // Get all places
    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces() {
        List<Place> places = placeService.getAllPlaces();
        return ResponseEntity.ok(places);
    }

    // Get place by ID
    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        Optional<Place> place = placeService.getPlaceById(id);
        return place.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get places by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Place>> getPlacesByCategory(@PathVariable PlaceCategory category) {
        List<Place> places = placeService.getPlacesByCategory(category);
        return ResponseEntity.ok(places);
    }

    // Get places by city
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Place>> getPlacesByCity(@PathVariable String city) {
        List<Place> places = placeService.getPlacesByCity(city);
        return ResponseEntity.ok(places);
    }

    // Search places by name
    @GetMapping("/search")
    public ResponseEntity<List<Place>> searchPlacesByName(@RequestParam String name) {
        List<Place> places = placeService.searchPlacesByName(name);
        return ResponseEntity.ok(places);
    }

    // Create new place (Admin only)
    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        // Validate required fields
        if (place.getName() == null || place.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Place savedPlace = placeService.createPlace(place);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlace);
    }

    // Update place (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id,
                                             @RequestBody Place placeDetails) {
        Optional<Place> existingPlace = placeService.getPlaceById(id);

        if (existingPlace.isPresent()) {
            Place place = existingPlace.get();
            place.setName(placeDetails.getName());
            place.setDescription(placeDetails.getDescription());
            place.setCategory(placeDetails.getCategory());
            place.setCity(placeDetails.getCity());
            place.setImageUrl(placeDetails.getImageUrl());
            place.setLatitude(placeDetails.getLatitude());
            place.setLongitude(placeDetails.getLongitude());

            Place updatedPlace = placeService.updatePlace(place);
            return ResponseEntity.ok(updatedPlace);
        }

        return ResponseEntity.notFound().build();
    }

    // Delete place (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        Optional<Place> place = placeService.getPlaceById(id);

        if (place.isPresent()) {
            placeService.deletePlace(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Get all available categories
    @GetMapping("/categories")
    public ResponseEntity<PlaceCategory[]> getAllCategories() {
        return ResponseEntity.ok(PlaceCategory.values());
    }
}