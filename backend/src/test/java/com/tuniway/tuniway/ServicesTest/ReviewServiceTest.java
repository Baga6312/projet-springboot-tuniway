package com.tuniway.tuniway.ServicesTest;

import com.tuniway.model.Review;
import com.tuniway.model.Place;
import com.tuniway.model.User;
import com.tuniway.repository.ReviewRepository;
import com.tuniway.service.ReviewService;
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
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;
    private Place testPlace;
    private User testUser;

    @BeforeEach
    void setUp() {
        testPlace = new Place();
        testPlace.setId(1L);
        testPlace.setName("Carthage");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setPlace(testPlace);
        testReview.setUser(testUser);
        testReview.setRating(5);
        testReview.setComment("Amazing place!");
        testReview.setDatePosted(LocalDateTime.now());
    }

    @Test
    void getAllReviews_ShouldReturnAllReviews() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findAll()).thenReturn(reviews);

        List<Review> result = reviewService.getAllReviews();

        assertThat(result).hasSize(1);
        assertThat(result).contains(testReview);
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getReviewById_WhenExists_ShouldReturnReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        Optional<Review> result = reviewService.getReviewById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getComment()).isEqualTo("Amazing place!");
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void getReviewById_WhenNotExists_ShouldReturnEmpty() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Review> result = reviewService.getReviewById(999L);

        assertThat(result).isEmpty();
        verify(reviewRepository, times(1)).findById(999L);
    }

    @Test
    void getReviewsByPlace_ShouldReturnPlaceReviews() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByPlace(testPlace)).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsByPlace(testPlace);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlace()).isEqualTo(testPlace);
        verify(reviewRepository, times(1)).findByPlace(testPlace);
    }

    @Test
    void getReviewsByRatingGreaterThanEqual_ShouldReturnMatchingReviews() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByRatingGreaterThanEqual(4)).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsByRatingGreaterThanEqual(4);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isGreaterThanOrEqualTo(4);
        verify(reviewRepository, times(1)).findByRatingGreaterThanEqual(4);
    }

    @Test
    void getReviewsByUser_ShouldReturnUserReviews() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByUser(testUser)).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsByUser(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser()).isEqualTo(testUser);
        verify(reviewRepository, times(1)).findByUser(testUser);
    }

    @Test
    void createReview_ShouldSetDateAndSave() {
        Review newReview = new Review();
        newReview.setPlace(testPlace);
        newReview.setUser(testUser);
        newReview.setRating(4);
        newReview.setComment("Good");

        when(reviewRepository.save(any(Review.class))).thenReturn(newReview);

        Review result = reviewService.createReview(newReview);

        assertThat(result.getDatePosted()).isNotNull();
        verify(reviewRepository, times(1)).save(newReview);
    }

    @Test
    void updateReview_ShouldSaveAndReturnUpdated() {
        testReview.setComment("Updated comment");
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.updateReview(testReview);

        assertThat(result.getComment()).isEqualTo("Updated comment");
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void deleteReview_ShouldCallRepositoryDelete() {
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }
}