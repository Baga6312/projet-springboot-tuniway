package com.tuniway.repository;

import com.tuniway.model.Place;
import com.tuniway.model.enums.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByCategory(PlaceCategory category);
    List<Place> findByCity(String city);
    List<Place> findByNameContainingIgnoreCase(String name);
}