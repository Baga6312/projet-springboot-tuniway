package com.tuniway.controller;

import com.tuniway.util.cache.CategoryData;
import com.tuniway.util.cache.PlaceCategoryFactory;
import com.tuniway.model.Place;
import com.tuniway.model.enums.PlaceCategory;
import com.tuniway.service.PlaceService;
import com.tuniway.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces() {
        List<Place> places = placeService.getAllPlaces();
        return ResponseEntity.ok(places);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        Optional<Place> place = placeService.getPlaceById(id);
        return place.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Place>> getPlacesByCategory(@PathVariable PlaceCategory category) {
        List<Place> places = placeService.getPlacesByCategory(category);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Place>> getPlacesByCity(@PathVariable String city) {
        List<Place> places = placeService.getPlacesByCity(city);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Place>> searchPlacesByName(@RequestParam String name) {
        List<Place> places = placeService.searchPlacesByName(name);
        return ResponseEntity.ok(places);
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        if (place.getName() == null || place.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Place savedPlace = placeService.createPlace(place);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlace);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlace(@PathVariable Long id) {
        Optional<Place> place = placeService.getPlaceById(id);

        if (!place.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (!reviewService.getReviewsByPlace(place.get()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete place: Place has existing reviews");
        }

        placeService.deletePlace(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<PlaceCategory[]> getAllCategories() {
        return ResponseEntity.ok(PlaceCategory.values());
    }
    @Autowired
    private PlaceCategoryFactory categoryFactory;



    // Lenna staamlna data li fil cache
    @GetMapping("/{id}/category-info")
    public ResponseEntity<Map<String, Object>> getPlaceCategoryInfo(@PathVariable Long id) {
        Optional<Place> place = placeService.getPlaceById(id);

        if (!place.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Place p = place.get();

        CategoryData categoryData = PlaceCategoryFactory.getCategoryData(p.getCategory());

        categoryData.display(p.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("placeName", p.getName());
        response.put("category", p.getCategory());
        response.put("icon", categoryData.getIcon());
        response.put("description", categoryData.getDescription());
        response.put("colorCode", categoryData.getColorCode());
        response.put("totalCategoriesInCache", PlaceCategoryFactory.getCacheSize());

        return ResponseEntity.ok(response);
    }
}
