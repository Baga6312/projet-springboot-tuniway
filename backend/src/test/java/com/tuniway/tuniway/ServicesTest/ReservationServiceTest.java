package com.tuniway.tuniway.ServicesTest;

import com.tuniway.model.Reservation;
import com.tuniway.model.Client;
import com.tuniway.model.Guide;
import com.tuniway.model.enums.ReservationStatus;
import com.tuniway.repository.ReservationRepository;
import com.tuniway.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private Client testClient;
    private Guide testGuide;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);

        testGuide = new Guide();
        testGuide.setId(2L);

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setClient(testClient);
        testReservation.setGuide(testGuide);
        testReservation.setStatus(ReservationStatus.PENDING);
        testReservation.setDateReservation(LocalDateTime.now());
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> result = reservationService.getAllReservations();

        assertThat(result).hasSize(1);
        assertThat(result).contains(testReservation);
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void getReservationById_WhenExists_ShouldReturnReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        Optional<Reservation> result = reservationService.getReservationById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getClient()).isEqualTo(testClient);
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void getReservationById_WhenNotExists_ShouldReturnEmpty() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Reservation> result = reservationService.getReservationById(999L);

        assertThat(result).isEmpty();
        verify(reservationRepository, times(1)).findById(999L);
    }

    @Test
    void getReservationsByClient_ShouldReturnClientReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByClient(testClient)).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByClient(testClient);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClient()).isEqualTo(testClient);
        verify(reservationRepository, times(1)).findByClient(testClient);
    }

    @Test
    void getReservationsByGuide_ShouldReturnGuideReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByGuide(testGuide)).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByGuide(testGuide);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGuide()).isEqualTo(testGuide);
        verify(reservationRepository, times(1)).findByGuide(testGuide);
    }

    @Test
    void getReservationsByStatus_ShouldReturnReservationsWithStatus() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByStatus(ReservationStatus.PENDING)).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByStatus(ReservationStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
        verify(reservationRepository, times(1)).findByStatus(ReservationStatus.PENDING);
    }

    @Test
    void createReservation_ShouldSetStatusToPendingAndSave() {
        Reservation newReservation = new Reservation();
        newReservation.setClient(testClient);
        newReservation.setGuide(testGuide);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(newReservation);

        Reservation result = reservationService.createReservation(newReservation);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
        verify(reservationRepository, times(1)).save(newReservation);
    }

    @Test
    void updateReservation_ShouldSaveAndReturnUpdated() {
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.updateReservation(testReservation);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void deleteReservation_ShouldCallRepositoryDelete() {
        doNothing().when(reservationRepository).deleteById(1L);

        reservationService.deleteReservation(1L);

        verify(reservationRepository, times(1)).deleteById(1L);
    }
}