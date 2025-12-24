package com.tuniway.controller;

import com.tuniway.model.Reservation;
import com.tuniway.model.Client;
import com.tuniway.model.Guide;
import com.tuniway.model.enums.ReservationStatus;
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
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    // Get all reservations
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // Get reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get reservations by client ID
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Reservation>> getReservationsByClient(@PathVariable Long clientId) {
        Optional<Client> client = userService.getUserById(clientId)
                .filter(u -> u instanceof Client)
                .map(u -> (Client) u);

        if (client.isPresent()) {
            List<Reservation> reservations = reservationService.getReservationsByClient(client.get());
            return ResponseEntity.ok(reservations);
        }

        return ResponseEntity.notFound().build();
    }

    // Get reservations by guide ID
    @GetMapping("/guide/{guideId}")
    public ResponseEntity<List<Reservation>> getReservationsByGuide(@PathVariable Long guideId) {
        Optional<Guide> guide = userService.getUserById(guideId)
                .filter(u -> u instanceof Guide)
                .map(u -> (Guide) u);

        if (guide.isPresent()) {
            List<Reservation> reservations = reservationService.getReservationsByGuide(guide.get());
            return ResponseEntity.ok(reservations);
        }

        return ResponseEntity.notFound().build();
    }

    // Get reservations by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        List<Reservation> reservations = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(reservations);
    }

    // Create new reservation
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        // Validate client exists
        Optional<Client> client = userService.getUserById(request.getClientId())
                .filter(u -> u instanceof Client)
                .map(u -> (Client) u);

        if (!client.isPresent()) {
            return ResponseEntity.badRequest().body("Client not found");
        }

        // Validate guide exists (if provided)
        Guide guide = null;
        if (request.getGuideId() != null) {
            Optional<Guide> guideOpt = userService.getUserById(request.getGuideId())
                    .filter(u -> u instanceof Guide)
                    .map(u -> (Guide) u);

            if (!guideOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Guide not found");
            }
            guide = guideOpt.get();
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setClient(client.get());
        reservation.setGuide(guide);
        reservation.setType(request.getType());
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationService.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // Update reservation status
    @PutMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(@PathVariable Long id,
                                                               @RequestBody StatusUpdate statusUpdate) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            reservation.setStatus(statusUpdate.getStatus());

            Reservation updatedReservation = reservationService.updateReservation(reservation);
            return ResponseEntity.ok(updatedReservation);
        }

        return ResponseEntity.notFound().build();
    }

    // Update entire reservation
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id,
                                                         @RequestBody Reservation reservationDetails) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            reservation.setStatus(reservationDetails.getStatus());
            reservation.setType(reservationDetails.getType());
            reservation.setDateReservation(reservationDetails.getDateReservation());

            Reservation updatedReservation = reservationService.updateReservation(reservation);
            return ResponseEntity.ok(updatedReservation);
        }

        return ResponseEntity.notFound().build();
    }

    // Cancel reservation
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            reservation.setStatus(ReservationStatus.CANCELLED);

            Reservation updatedReservation = reservationService.updateReservation(reservation);
            return ResponseEntity.ok(updatedReservation);
        }

        return ResponseEntity.notFound().build();
    }

    // Confirm reservation
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Reservation> confirmReservation(@PathVariable Long id) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            reservation.setStatus(ReservationStatus.CONFIRMED);

            Reservation updatedReservation = reservationService.updateReservation(reservation);
            return ResponseEntity.ok(updatedReservation);
        }

        return ResponseEntity.notFound().build();
    }

    // Complete reservation
    @PutMapping("/{id}/complete")
    public ResponseEntity<Reservation> completeReservation(@PathVariable Long id) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            reservation.setStatus(ReservationStatus.COMPLETED);

            Reservation updatedReservation = reservationService.updateReservation(reservation);
            return ResponseEntity.ok(updatedReservation);
        }

        return ResponseEntity.notFound().build();
    }

    // Delete reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);

        if (reservation.isPresent()) {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Inner class for reservation creation request
    static class ReservationRequest {
        private Long clientId;
        private Long guideId;
        private com.tuniway.model.enums.ReservationType type;

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        public Long getGuideId() {
            return guideId;
        }

        public void setGuideId(Long guideId) {
            this.guideId = guideId;
        }

        public com.tuniway.model.enums.ReservationType getType() {
            return type;
        }

        public void setType(com.tuniway.model.enums.ReservationType type) {
            this.type = type;
        }
    }

    // Inner class for status update
    static class StatusUpdate {
        private ReservationStatus status;

        public ReservationStatus getStatus() {
            return status;
        }

        public void setStatus(ReservationStatus status) {
            this.status = status;
        }
    }
}