package com.tuniway.tuniway.ServicesTest;

import com.tuniway.model.Place;
import com.tuniway.model.enums.PlaceCategory;
import com.tuniway.repository.PlaceRepository;
import com.tuniway.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceService placeService;

    private Place testPlace;

    @BeforeEach
    void setUp() {
        testPlace = new Place();
        testPlace.setId(1L);
        testPlace.setName("Carthage");
        testPlace.setDescription("Ancient ruins");
        testPlace.setCategory(PlaceCategory.HISTORICAL);
        testPlace.setCity("Tunis");
        testPlace.setLatitude(36.8525);
        testPlace.setLongitude(10.3233);
    }

    @Test
    void getAllPlaces_ShouldReturnAllPlaces() {
        List<Place> expectedPlaces = Arrays.asList(testPlace);
        when(placeRepository.findAll()).thenReturn(expectedPlaces);

        List<Place> result = placeService.getAllPlaces();

        assertThat(result).hasSize(1);
        assertThat(result).contains(testPlace);
        verify(placeRepository, times(1)).findAll();
    }

    @Test
    void getPlaceById_WhenExists_ShouldReturnPlace() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(testPlace));

        Optional<Place> result = placeService.getPlaceById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Carthage");
        verify(placeRepository, times(1)).findById(1L);
    }

    @Test
    void getPlaceById_WhenNotExists_ShouldReturnEmpty() {
        when(placeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Place> result = placeService.getPlaceById(999L);

        assertThat(result).isEmpty();
        verify(placeRepository, times(1)).findById(999L);
    }

    @Test
    void getPlacesByCategory_ShouldReturnPlacesInCategory() {
        List<Place> places = Arrays.asList(testPlace);
        when(placeRepository.findByCategory(PlaceCategory.HISTORICAL)).thenReturn(places);

        List<Place> result = placeService.getPlacesByCategory(PlaceCategory.HISTORICAL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(PlaceCategory.HISTORICAL);
        verify(placeRepository, times(1)).findByCategory(PlaceCategory.HISTORICAL);
    }

    @Test
    void getPlacesByCity_ShouldReturnPlacesInCity() {
        List<Place> places = Arrays.asList(testPlace);
        when(placeRepository.findByCity("Tunis")).thenReturn(places);

        List<Place> result = placeService.getPlacesByCity("Tunis");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Tunis");
        verify(placeRepository, times(1)).findByCity("Tunis");
    }

    @Test
    void searchPlacesByName_ShouldReturnMatchingPlaces() {
        List<Place> places = Arrays.asList(testPlace);
        when(placeRepository.findByNameContainingIgnoreCase("Carthage")).thenReturn(places);

        List<Place> result = placeService.searchPlacesByName("Carthage");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Carthage");
        verify(placeRepository, times(1)).findByNameContainingIgnoreCase("Carthage");
    }

    @Test
    void createPlace_ShouldSaveAndReturnPlace() {
        when(placeRepository.save(any(Place.class))).thenReturn(testPlace);

        Place result = placeService.createPlace(testPlace);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Carthage");
        verify(placeRepository, times(1)).save(testPlace);
    }

    @Test
    void updatePlace_ShouldSaveAndReturnUpdated() {
        testPlace.setDescription("Updated description");
        when(placeRepository.save(any(Place.class))).thenReturn(testPlace);

        Place result = placeService.updatePlace(testPlace);

        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(placeRepository, times(1)).save(testPlace);
    }

    @Test
    void deletePlace_ShouldCallRepositoryDelete() {
        doNothing().when(placeRepository).deleteById(1L);

        placeService.deletePlace(1L);

        verify(placeRepository, times(1)).deleteById(1L);
    }
}