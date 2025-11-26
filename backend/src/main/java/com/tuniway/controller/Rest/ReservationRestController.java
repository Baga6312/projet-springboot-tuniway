package com.tuniway.controller.Rest;


import com.tuniway.model.*;
import com.tuniway.model.enums.ReservationStatus;
import com.tuniway.model.enums.ReservationType;
import com.tuniway.service.ReservationService;
import com.tuniway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rest/reservations")
@CrossOrigin(origins = "*")
public class ReservationRestController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> ajouterReservation(@RequestBody ReservationRequest request) {
        Optional<Client> client = userService.getUserById(request.getClientId())
                .filter(u -> u instanceof Client)
                .map(u -> (Client) u);
        if (!client.isPresent()) {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
        }

        Guide guide = null;
        if (request.getGuideId() != null) {
            Optional<Guide> guideOpt = userService.getUserById(request.getGuideId())
                    .filter(u -> u instanceof Guide)
                    .map(u -> (Guide) u);
            if (!guideOpt.isPresent()) {
                return new ResponseEntity<>("Guide not found", HttpStatus.NOT_FOUND);
            }
            guide = guideOpt.get();
        }

        Reservation reservation = new Reservation();
        reservation.setClient(client.get());
        reservation.setGuide(guide);
        reservation.setType(request.getType());
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationService.createReservation(reservation);
        return new ResponseEntity<>(savedReservation, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> listReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id,
                                                         @RequestBody Reservation reservationDetails) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (!existingReservation.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Reservation reservation = existingReservation.get();
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setType(reservationDetails.getType());

        Reservation updatedReservation = reservationService.updateReservation(reservation);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);

        if (!reservation.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        reservationService.deleteReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    static class ReservationRequest {
        private Long clientId;
        private Long guideId;
        private ReservationType type;

        public Long getClientId() { return clientId; }
        public void setClientId(Long clientId) { this.clientId = clientId; }
        public Long getGuideId() { return guideId; }
        public void setGuideId(Long guideId) { this.guideId = guideId; }
        public ReservationType getType() { return type; }
        public void setType(ReservationType type) { this.type = type; }
    }
}