package com.tuniway.controller.Rest;


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
@RequestMapping("/api/rest/reviews")
@CrossOrigin(origins = "*")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> ajouterReview(@RequestBody ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            return new ResponseEntity<>("Rating must be between 1 and 5", HttpStatus.BAD_REQUEST);
        }

        Optional<Place> place = placeService.getPlaceById(request.getPlaceId());
        if (!place.isPresent()) {
            return new ResponseEntity<>("Place not found", HttpStatus.NOT_FOUND);
        }

        Optional<User> user = userService.getUserById(request.getUserId());
        if (!user.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Review review = new Review();
        review.setPlace(place.get());
        review.setUser(user.get());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setDatePosted(LocalDateTime.now());

        Review savedReview = reviewService.createReview(review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Review>> listReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewService.getReviewById(id);
        if (review.isPresent()) {
            return new ResponseEntity<>(review.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewUpdateRequest request) {
        Optional<Review> existingReview = reviewService.getReviewById(id);

        if (!existingReview.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            return new ResponseEntity<>("Rating must be between 1 and 5", HttpStatus.BAD_REQUEST);
        }

        Review review = existingReview.get();
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        Review updatedReview = reviewService.updateReview(review);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        Optional<Review> review = reviewService.getReviewById(id);

        if (!review.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<Review>> getReviewsByPlace(@PathVariable Long placeId) {
        Optional<Place> place = placeService.getPlaceById(placeId);
        if (!place.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Review> reviews = reviewService.getReviewsByPlace(place.get());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    static class ReviewRequest {
        private Long placeId;
        private Long userId;
        private Integer rating;
        private String comment;

        public Long getPlaceId() { return placeId; }
        public void setPlaceId(Long placeId) { this.placeId = placeId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    static class ReviewUpdateRequest {
        private Integer rating;
        private String comment;

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}