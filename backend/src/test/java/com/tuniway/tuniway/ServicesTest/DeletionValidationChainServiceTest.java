package com.tuniway.tuniway.ServicesTest;

import com.tuniway.model.*;
import com.tuniway.service.*;
import com.tuniway.util.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletionValidationChainServiceTest {

    @Mock
    private ReviewService reviewService;
    @Mock
    private ReservationService reservationService;
    @Mock
    private TourPersonnaliseService tourPersonnaliseService;

    private DeletionValidationChainService chainService;

    private Client testClient;
    private Guide testGuide;

    @BeforeEach
    void setUp() {
        // Create handlers
        ReviewCheckHandler reviewCheckHandler = new ReviewCheckHandler(reviewService);
        ClientReservationCheckHandler clientReservationCheckHandler =
                new ClientReservationCheckHandler(reservationService);
        ClientTourCheckHandler clientTourCheckHandler =
                new ClientTourCheckHandler(tourPersonnaliseService);
        GuideReservationCheckHandler guideReservationCheckHandler =
                new GuideReservationCheckHandler(reservationService);
        GuideTourCheckHandler guideTourCheckHandler =
                new GuideTourCheckHandler(tourPersonnaliseService);

        // Create service
        chainService = new DeletionValidationChainService(
                reviewCheckHandler,
                clientReservationCheckHandler,
                clientTourCheckHandler,
                guideReservationCheckHandler,
                guideTourCheckHandler
        );

        testClient = new Client();
        testClient.setId(1L);
        testClient.setUsername("testclient");

        testGuide = new Guide();
        testGuide.setId(2L);
        testGuide.setUsername("testguide");
    }

    @Test
    void validateDeletion_WhenUserHasNoRelations_ShouldAllowDeletion() {
        when(reviewService.getReviewsByUser(testClient)).thenReturn(Collections.emptyList());
        when(reservationService.getReservationsByClient(testClient)).thenReturn(Collections.emptyList());
        when(tourPersonnaliseService.getToursByClient(testClient)).thenReturn(Collections.emptyList());

        DeletionResult result = chainService.validateDeletion(testClient);

        assertThat(result.isCanDelete()).isTrue();
        assertThat(result.getMessage()).isEqualTo("User can be deleted");
    }

    @Test
    void validateDeletion_WhenClientHasReviews_ShouldPreventDeletion() {
        Review review = new Review();
        when(reviewService.getReviewsByUser(testClient)).thenReturn(Collections.singletonList(review));

        DeletionResult result = chainService.validateDeletion(testClient);

        assertThat(result.isCanDelete()).isFalse();
        assertThat(result.getMessage()).contains("existing reviews");
    }

    @Test
    void validateDeletion_WhenClientHasReservations_ShouldPreventDeletion() {
        when(reviewService.getReviewsByUser(testClient)).thenReturn(Collections.emptyList());

        Reservation reservation = new Reservation();
        when(reservationService.getReservationsByClient(testClient))
                .thenReturn(Collections.singletonList(reservation));

        DeletionResult result = chainService.validateDeletion(testClient);

        assertThat(result.isCanDelete()).isFalse();
        assertThat(result.getMessage()).contains("existing reservations");
    }

    @Test
    void validateDeletion_WhenClientHasTours_ShouldPreventDeletion() {
        when(reviewService.getReviewsByUser(testClient)).thenReturn(Collections.emptyList());
        when(reservationService.getReservationsByClient(testClient)).thenReturn(Collections.emptyList());

        TourPersonnalise tour = new TourPersonnalise();
        when(tourPersonnaliseService.getToursByClient(testClient))
                .thenReturn(Collections.singletonList(tour));

        DeletionResult result = chainService.validateDeletion(testClient);

        assertThat(result.isCanDelete()).isFalse();
        assertThat(result.getMessage()).contains("existing personalized tours");
    }

    @Test
    void validateDeletion_WhenGuideHasReservations_ShouldPreventDeletion() {
        when(reviewService.getReviewsByUser(testGuide)).thenReturn(Collections.emptyList());

        Reservation reservation = new Reservation();
        when(reservationService.getReservationsByGuide(testGuide))
                .thenReturn(Collections.singletonList(reservation));

        DeletionResult result = chainService.validateDeletion(testGuide);

        assertThat(result.isCanDelete()).isFalse();
        assertThat(result.getMessage()).contains("existing reservations");
    }

    @Test
    void validateDeletion_WhenGuideHasTours_ShouldPreventDeletion() {
        when(reviewService.getReviewsByUser(testGuide)).thenReturn(Collections.emptyList());
        when(reservationService.getReservationsByGuide(testGuide)).thenReturn(Collections.emptyList());

        TourPersonnalise tour = new TourPersonnalise();
        when(tourPersonnaliseService.getToursByGuide(testGuide))
                .thenReturn(Collections.singletonList(tour));

        DeletionResult result = chainService.validateDeletion(testGuide);

        assertThat(result.isCanDelete()).isFalse();
        assertThat(result.getMessage()).contains("existing personalized tours");
    }

    @Test
    void getChainDescription_ShouldReturnValidDescription() {
        String description = chainService.getChainDescription();

        assertThat(description).contains("Review Check");
        assertThat(description).contains("Client Reservation Check");
        assertThat(description).contains("Client Tour Check");
        assertThat(description).contains("Guide Reservation Check");
        assertThat(description).contains("Guide Tour Check");
    }
}