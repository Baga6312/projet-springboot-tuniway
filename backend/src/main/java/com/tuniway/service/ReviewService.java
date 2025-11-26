package com.tuniway.service;

import com.tuniway.model.Review;
import com.tuniway.model.Place;
import com.tuniway.model.User;
import com.tuniway.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByPlace(Place place) {
        return reviewRepository.findByPlace(place);
    }

    public List<Review> getReviewsByRatingGreaterThanEqual(Integer rating) {
        return reviewRepository.findByRatingGreaterThanEqual(rating);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    public Review createReview(Review review) {
        review.setDatePosted(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}