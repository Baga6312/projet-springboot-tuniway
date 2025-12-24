package com.tuniway.controller;

import com.tuniway.model.Review;
import com.tuniway.model.Place;
import com.tuniway.model.User;
import com.tuniway.service.ReviewService;
import com.tuniway.service.PlaceService;
import com.tuniway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<Review>> getReviewsByPlace(@PathVariable Long placeId) {
        Optional<Place> place = placeService.getPlaceById(placeId);

        if (place.isPresent()) {
            List<Review> reviews = reviewService.getReviewsByPlace(place.get());
            return ResponseEntity.ok(reviews);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);

        if (user.isPresent()) {
            List<Review> reviews = reviewService.getReviewsByUser(user.get());
            return ResponseEntity.ok(reviews);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<Review>> getReviewsByMinRating(@PathVariable Integer minRating) {
        if (minRating < 1 || minRating > 5) {
            return ResponseEntity.badRequest().build();
        }

        List<Review> reviews = reviewService.getReviewsByRatingGreaterThanEqual(minRating);
        return ResponseEntity.ok(reviews);
    }


    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }


        Optional<Place> place = placeService.getPlaceById(request.getPlaceId());
        if (!place.isPresent()) {
            return ResponseEntity.badRequest().body("Place not found");
        }


        Optional<User> user = userService.getUserById(request.getUserId());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Review review = new Review();
        review.setPlace(place.get());
        review.setUser(user.get());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setDatePosted(LocalDateTime.now());

        Review savedReview = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                          @RequestBody ReviewUpdateRequest request) {
        Optional<Review> existingReview = reviewService.getReviewById(id);

        if (!existingReview.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        Review review = existingReview.get();

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        Review updatedReview = reviewService.updateReview(review);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        Optional<Review> review = reviewService.getReviewById(id);

        if (review.isPresent()) {
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Get average rating for a place
    @GetMapping("/place/{placeId}/average")
    public ResponseEntity<AverageRatingResponse> getAverageRating(@PathVariable Long placeId) {
        Optional<Place> place = placeService.getPlaceById(placeId);

        if (!place.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = reviewService.getReviewsByPlace(place.get());

        if (reviews.isEmpty()) {
            return ResponseEntity.ok(new AverageRatingResponse(0.0, 0));
        }

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return ResponseEntity.ok(new AverageRatingResponse(average, reviews.size()));
    }

    // Inner class for review creation request
    static class ReviewRequest {
        private Long placeId;
        private Long userId;
        private Integer rating;
        private String comment;

        public Long getPlaceId() {
            return placeId;
        }

        public void setPlaceId(Long placeId) {
            this.placeId = placeId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    // Inner class for review update request
    static class ReviewUpdateRequest {
        private Integer rating;
        private String comment;

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    // Inner class for average rating response
    static class AverageRatingResponse {
        private Double averageRating;
        private Integer totalReviews;

        public AverageRatingResponse(Double averageRating, Integer totalReviews) {
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }

        public Double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(Double averageRating) {
            this.averageRating = averageRating;
        }

        public Integer getTotalReviews() {
            return totalReviews;
        }

        public void setTotalReviews(Integer totalReviews) {
            this.totalReviews = totalReviews;
        }
    }
}