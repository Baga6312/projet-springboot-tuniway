package com.tuniway.util.validation;

import com.tuniway.model.Client;
import com.tuniway.model.User;
import com.tuniway.service.ReservationService;
import org.springframework.stereotype.Component;

@Component
public class ClientReservationCheckHandler extends DeletionHandler {
    private final ReservationService reservationService;

    public ClientReservationCheckHandler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public DeletionResult canDelete(User user) {
        if (user instanceof Client) {
            Client client = (Client) user;
            if (!reservationService.getReservationsByClient(client).isEmpty()) {
                return new DeletionResult(false, "Cannot delete client: Client has existing reservations");
            }
        }
        return checkNext(user);
    }
}