package com.tuniway.controller.Rest;

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
@RequestMapping("/api/rest/places")
@CrossOrigin(origins = "*")
public class PlaceRestController {

    @Autowired
    private PlaceService placeService;

    @PostMapping
    public ResponseEntity<Place> ajouterPlace(@RequestBody Place place) {
        if (place.getName() == null || place.getName().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Place savedPlace = placeService.createPlace(place);
        return new ResponseEntity<>(savedPlace, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Place>> listPlaces() {
        List<Place> places = placeService.getAllPlaces();
        return new ResponseEntity<>(places, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        Optional<Place> place = placeService.getPlaceById(id);
        if (place.isPresent()) {
            return new ResponseEntity<>(place.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestBody Place placeDetails) {
        Optional<Place> existingPlace = placeService.getPlaceById(id);

        if (!existingPlace.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Place place = existingPlace.get();
        place.setName(placeDetails.getName());
        place.setDescription(placeDetails.getDescription());
        place.setCategory(placeDetails.getCategory());
        place.setCity(placeDetails.getCity());
        place.setImageUrl(placeDetails.getImageUrl());
        place.setLatitude(placeDetails.getLatitude());
        place.setLongitude(placeDetails.getLongitude());

        Place updatedPlace = placeService.updatePlace(place);
        return new ResponseEntity<>(updatedPlace, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        Optional<Place> place = placeService.getPlaceById(id);

        if (!place.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        placeService.deletePlace(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Place>> getPlacesByCategory(@PathVariable PlaceCategory category) {
        List<Place> places = placeService.getPlacesByCategory(category);
        return new ResponseEntity<>(places, HttpStatus.OK);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Place>> getPlacesByCity(@PathVariable String city) {
        List<Place> places = placeService.getPlacesByCity(city);
        return new ResponseEntity<>(places, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Place>> searchPlaces(@RequestParam String name) {
        List<Place> places = placeService.searchPlacesByName(name);
        return new ResponseEntity<>(places, HttpStatus.OK);
    }
}