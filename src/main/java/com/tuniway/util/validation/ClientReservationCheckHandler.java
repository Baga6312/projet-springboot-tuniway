package com.tuniway.util.validation;

import com.tuniway.model.Client;
import com.tuniway.model.Utilisateur;
import com.tuniway.service.ReservationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class ClientReservationCheckHandler extends DeletionHandler {
    private final ReservationService reservationService;

    public ClientReservationCheckHandler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public DeletionResult canDelete(Utilisateur user) {
        if (user instanceof Client) {
            Client client = (Client) user;
            if (!reservationService.getReservationsByClient(client).isEmpty()) {
                return new DeletionResult(false, "Cannot delete client: Client has existing reservations");
            }
        }
        return checkNext(user);
    }
}