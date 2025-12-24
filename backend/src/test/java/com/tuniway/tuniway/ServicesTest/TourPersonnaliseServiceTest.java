package com.tuniway.tuniway.ServicesTest;

import com.tuniway.model.TourPersonnalise;
import com.tuniway.model.Guide;
import com.tuniway.model.Client;
import com.tuniway.repository.TourPersonnaliseRepository;
import com.tuniway.service.TourPersonnaliseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourPersonnaliseServiceTest {

    @Mock
    private TourPersonnaliseRepository tourPersonnaliseRepository;

    @InjectMocks
    private TourPersonnaliseService tourPersonnaliseService;

    private TourPersonnalise testTour;
    private Guide testGuide;
    private Client testClient;

    @BeforeEach
    void setUp() {
        testGuide = new Guide();
        testGuide.setId(1L);
        testGuide.setUsername("guidename");

        testClient = new Client();
        testClient.setId(2L);
        testClient.setUsername("clientname");

        testTour = new TourPersonnalise();
        testTour.setId(1L);
        testTour.setTitre("Tunisia Tour");
        testTour.setDescription("Custom tour");
        testTour.setPrix(150.0);
        testTour.setDate(LocalDate.now());
        testTour.setGuide(testGuide);
        testTour.setClient(testClient);
    }

    @Test
    void getAllTours_ShouldReturnAllTours() {
        List<TourPersonnalise> tours = Arrays.asList(testTour);
        when(tourPersonnaliseRepository.findAll()).thenReturn(tours);

        List<TourPersonnalise> result = tourPersonnaliseService.getAllTours();

        assertThat(result).hasSize(1);
        assertThat(result).contains(testTour);
        verify(tourPersonnaliseRepository, times(1)).findAll();
    }

    @Test
    void getTourById_WhenExists_ShouldReturnTour() {
        when(tourPersonnaliseRepository.findById(1L)).thenReturn(Optional.of(testTour));

        Optional<TourPersonnalise> result = tourPersonnaliseService.getTourById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitre()).isEqualTo("Tunisia Tour");
        verify(tourPersonnaliseRepository, times(1)).findById(1L);
    }

    @Test
    void getTourById_WhenNotExists_ShouldReturnEmpty() {
        when(tourPersonnaliseRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<TourPersonnalise> result = tourPersonnaliseService.getTourById(999L);

        assertThat(result).isEmpty();
        verify(tourPersonnaliseRepository, times(1)).findById(999L);
    }

    @Test
    void getToursByGuide_ShouldReturnGuideTours() {
        List<TourPersonnalise> tours = Arrays.asList(testTour);
        when(tourPersonnaliseRepository.findByGuide(testGuide)).thenReturn(tours);

        List<TourPersonnalise> result = tourPersonnaliseService.getToursByGuide(testGuide);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGuide()).isEqualTo(testGuide);
        verify(tourPersonnaliseRepository, times(1)).findByGuide(testGuide);
    }

    @Test
    void getToursByClient_ShouldReturnClientTours() {
        List<TourPersonnalise> tours = Arrays.asList(testTour);
        when(tourPersonnaliseRepository.findByClient(testClient)).thenReturn(tours);

        List<TourPersonnalise> result = tourPersonnaliseService.getToursByClient(testClient);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClient()).isEqualTo(testClient);
        verify(tourPersonnaliseRepository, times(1)).findByClient(testClient);
    }

    @Test
    void createTour_ShouldSaveAndReturnTour() {
        when(tourPersonnaliseRepository.save(any(TourPersonnalise.class))).thenReturn(testTour);

        TourPersonnalise result = tourPersonnaliseService.createTour(testTour);

        assertThat(result).isNotNull();
        assertThat(result.getTitre()).isEqualTo("Tunisia Tour");
        verify(tourPersonnaliseRepository, times(1)).save(testTour);
    }

    @Test
    void updateTour_ShouldSaveAndReturnUpdated() {
        testTour.setPrix(200.0);
        when(tourPersonnaliseRepository.save(any(TourPersonnalise.class))).thenReturn(testTour);

        TourPersonnalise result = tourPersonnaliseService.updateTour(testTour);

        assertThat(result.getPrix()).isEqualTo(200.0);
        verify(tourPersonnaliseRepository, times(1)).save(testTour);
    }

    @Test
    void deleteTour_ShouldCallRepositoryDelete() {
        doNothing().when(tourPersonnaliseRepository).deleteById(1L);

        tourPersonnaliseService.deleteTour(1L);

        verify(tourPersonnaliseRepository, times(1)).deleteById(1L);
    }
}