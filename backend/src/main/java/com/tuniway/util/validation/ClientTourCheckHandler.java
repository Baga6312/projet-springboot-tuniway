package com.tuniway.util.validation;

import com.tuniway.model.Client;
import com.tuniway.model.User;
import com.tuniway.service.TourPersonnaliseService;
import org.springframework.stereotype.Component;

@Component
public class ClientTourCheckHandler extends DeletionHandler {
    private final TourPersonnaliseService tourPersonnaliseService;

    public ClientTourCheckHandler(TourPersonnaliseService tourPersonnaliseService) {
        this.tourPersonnaliseService = tourPersonnaliseService;
    }

    @Override
    public DeletionResult canDelete(User user) {
        if (user instanceof Client) {
            Client client = (Client) user;
            if (!tourPersonnaliseService.getToursByClient(client).isEmpty()) {
                return new DeletionResult(false, "Cannot delete client: Client has existing personalized tours");
            }
        }
        return checkNext(user);
    }
}