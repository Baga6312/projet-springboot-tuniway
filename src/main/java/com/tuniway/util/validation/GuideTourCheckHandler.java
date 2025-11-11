package com.tuniway.util.validation;

import com.tuniway.model.Guide;
import com.tuniway.model.Utilisateur;
import com.tuniway.service.TourPersonnaliseService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class GuideTourCheckHandler extends DeletionHandler {
    private final TourPersonnaliseService tourPersonnaliseService;

    public GuideTourCheckHandler(TourPersonnaliseService tourPersonnaliseService) {
        this.tourPersonnaliseService = tourPersonnaliseService;
    }

    @Override
    public DeletionResult canDelete(Utilisateur user) {
        if (user instanceof Guide) {
            Guide guide = (Guide) user;
            if (!tourPersonnaliseService.getToursByGuide(guide).isEmpty()) {
                return new DeletionResult(false, "Cannot delete guide: Guide has existing personalized tours");
            }
        }
        return checkNext(user);
    }
}