package com.tuniway.chain;

import com.tuniway.model.Utilisateur;
import com.tuniway.service.ReviewService;
import org.springframework.stereotype.Component;

@Component
public class ReviewCheckHandler extends DeletionHandler {
    private final ReviewService reviewService;

    public ReviewCheckHandler(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public DeletionResult canDelete(Utilisateur user) {
        if (!reviewService.getReviewsByUser(user).isEmpty()) {
            return new DeletionResult(false, "Cannot delete user: User has existing reviews");
        }
        return checkNext(user);
    }
}
