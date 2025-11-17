package com.tuniway.repository;

import com.tuniway.model.Review;
import com.tuniway.model.Place;
import com.tuniway.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPlace(Place place);
    List<Review> findByUser(Utilisateur user);
    List<Review> findByRatingGreaterThanEqual(Integer rating);
}