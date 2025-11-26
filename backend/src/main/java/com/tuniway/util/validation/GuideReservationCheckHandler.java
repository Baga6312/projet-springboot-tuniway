package com.tuniway.util.validation;

import com.tuniway.model.Guide;
import com.tuniway.model.User;
import com.tuniway.service.ReservationService;
import org.springframework.stereotype.Component;

@Component
public class GuideReservationCheckHandler extends DeletionHandler {
    private final ReservationService reservationService;

    public GuideReservationCheckHandler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public DeletionResult canDelete(User user) {
        if (user instanceof Guide) {
            Guide guide = (Guide) user;
            if (!reservationService.getReservationsByGuide(guide).isEmpty()) {
                return new DeletionResult(false, "Cannot delete guide: Guide has existing reservations");
            }
        }
        return checkNext(user);
    }
}