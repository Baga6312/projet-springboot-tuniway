package com.tuniway.util.validation;

import com.tuniway.model.Client;
import com.tuniway.model.Utilisateur;
import com.tuniway.service.TourPersonnaliseService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class ClientTourCheckHandler extends DeletionHandler {
    private final TourPersonnaliseService tourPersonnaliseService;

    public ClientTourCheckHandler(TourPersonnaliseService tourPersonnaliseService) {
        this.tourPersonnaliseService = tourPersonnaliseService;
    }

    @Override
    public DeletionResult canDelete(Utilisateur user) {
        if (user instanceof Client) {
            Client client = (Client) user;
            if (!tourPersonnaliseService.getToursByClient(client).isEmpty()) {
                return new DeletionResult(false, "Cannot delete client: Client has existing personalized tours");
            }
        }
        return checkNext(user);
    }
}