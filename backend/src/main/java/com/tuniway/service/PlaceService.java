package com.tuniway.service;

import com.tuniway.model.Place;
import com.tuniway.model.enums.PlaceCategory;
import com.tuniway.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> getAllPlaces() {

        return placeRepository.findAll();
    }

    public Optional<Place> getPlaceById(Long id) {

        return placeRepository.findById(id);
    }

    public List<Place> getPlacesByCategory(PlaceCategory category) {

        return placeRepository.findByCategory(category);
    }

    public List<Place> getPlacesByCity(String city) {

        return placeRepository.findByCity(city);
    }

    public List<Place> searchPlacesByName(String name) {

        return placeRepository.findByNameContainingIgnoreCase(name);
    }

    public Place createPlace(Place place) {

        return placeRepository.save(place);
    }

    public Place updatePlace(Place place) {

        return placeRepository.save(place);
    }

    public void deletePlace(Long id) {

        placeRepository.deleteById(id);
    }
}