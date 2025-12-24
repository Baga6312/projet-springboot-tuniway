package com.tuniway.controller;

import com.tuniway.model.TourPersonnalise;
import com.tuniway.model.Client;
import com.tuniway.model.Guide;
import com.tuniway.service.TourPersonnaliseService;
import com.tuniway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "*")
public class TourPersonnaliseController {

    @Autowired
    private TourPersonnaliseService tourPersonnaliseService;

    @Autowired
    private UserService userService;

    // Get all tours
    @GetMapping
    public ResponseEntity<List<TourPersonnalise>> getAllTours() {
        List<TourPersonnalise> tours = tourPersonnaliseService.getAllTours();
        return ResponseEntity.ok(tours);
    }

    // Get tour by ID
    @GetMapping("/{id}")
    public ResponseEntity<TourPersonnalise> getTourById(@PathVariable Long id) {
        Optional<TourPersonnalise> tour = tourPersonnaliseService.getTourById(id);
        return tour.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get tours by guide ID
    @GetMapping("/guide/{guideId}")
    public ResponseEntity<List<TourPersonnalise>> getToursByGuide(@PathVariable Long guideId) {
        Optional<Guide> guide = userService.getUserById(guideId)
                .filter(u -> u instanceof Guide)
                .map(u -> (Guide) u);

        if (guide.isPresent()) {
            List<TourPersonnalise> tours = tourPersonnaliseService.getToursByGuide(guide.get());
            return ResponseEntity.ok(tours);
        }

        return ResponseEntity.notFound().build();
    }

    // Get tours by client ID
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<TourPersonnalise>> getToursByClient(@PathVariable Long clientId) {
        Optional<Client> client = userService.getUserById(clientId)
                .filter(u -> u instanceof Client)
                .map(u -> (Client) u);

        if (client.isPresent()) {
            List<TourPersonnalise> tours = tourPersonnaliseService.getToursByClient(client.get());
            return ResponseEntity.ok(tours);
        }

        return ResponseEntity.notFound().build();
    }

    // Create new tour
    @PostMapping
    public ResponseEntity<?> createTour(@RequestBody TourRequest request) {
        // Validate guide exists
        Optional<Guide> guide = userService.getUserById(request.getGuideId())
                .filter(u -> u instanceof Guide)
                .map(u -> (Guide) u);

        if (!guide.isPresent()) {
            return ResponseEntity.badRequest().body("Guide not found");
        }

        // Validate client exists
        Optional<Client> client = userService.getUserById(request.getClientId())
                .filter(u -> u instanceof Client)
                .map(u -> (Client) u);

        if (!client.isPresent()) {
            return ResponseEntity.badRequest().body("Client not found");
        }

        // Validate required fields
        if (request.getTitre() == null || request.getTitre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tour title is required");
        }

        if (request.getPrix() == null || request.getPrix() < 0) {
            return ResponseEntity.badRequest().body("Valid price is required");
        }

        // Create tour
        TourPersonnalise tour = new TourPersonnalise();
        tour.setGuide(guide.get());
        tour.setClient(client.get());
        tour.setTitre(request.getTitre());
        tour.setDescription(request.getDescription());
        tour.setPrix(request.getPrix());
        tour.setDate(request.getDate());

        TourPersonnalise savedTour = tourPersonnaliseService.createTour(tour);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTour);
    }

    // Update tour
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTour(@PathVariable Long id,
                                        @RequestBody TourUpdateRequest request) {
        Optional<TourPersonnalise> existingTour = tourPersonnaliseService.getTourById(id);

        if (!existingTour.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        TourPersonnalise tour = existingTour.get();

        // Update guide if provided
        if (request.getGuideId() != null) {
            Optional<Guide> guide = userService.getUserById(request.getGuideId())
                    .filter(u -> u instanceof Guide)
                    .map(u -> (Guide) u);

            if (!guide.isPresent()) {
                return ResponseEntity.badRequest().body("Guide not found");
            }
            tour.setGuide(guide.get());
        }

        // Update fields if provided
        if (request.getTitre() != null) {
            tour.setTitre(request.getTitre());
        }

        if (request.getDescription() != null) {
            tour.setDescription(request.getDescription());
        }

        if (request.getPrix() != null) {
            if (request.getPrix() < 0) {
                return ResponseEntity.badRequest().body("Price cannot be negative");
            }
            tour.setPrix(request.getPrix());
        }

        if (request.getDate() != null) {
            tour.setDate(request.getDate());
        }

        TourPersonnalise updatedTour = tourPersonnaliseService.updateTour(tour);
        return ResponseEntity.ok(updatedTour);
    }

    // Delete tour
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        Optional<TourPersonnalise> tour = tourPersonnaliseService.getTourById(id);

        if (tour.isPresent()) {
            tourPersonnaliseService.deleteTour(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Inner class for tour creation request
    static class TourRequest {
        private Long guideId;
        private Long clientId;
        private String titre;
        private String description;
        private Double prix;
        private java.time.LocalDate date;

        public Long getGuideId() {
            return guideId;
        }

        public void setGuideId(Long guideId) {
            this.guideId = guideId;
        }

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getPrix() {
            return prix;
        }

        public void setPrix(Double prix) {
            this.prix = prix;
        }

        public java.time.LocalDate getDate() {
            return date;
        }

        public void setDate(java.time.LocalDate date) {
            this.date = date;
        }
    }

    // Inner class for tour update request
    static class TourUpdateRequest {
        private Long guideId;
        private String titre;
        private String description;
        private Double prix;
        private java.time.LocalDate date;

        public Long getGuideId() {
            return guideId;
        }

        public void setGuideId(Long guideId) {
            this.guideId = guideId;
        }

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getPrix() {
            return prix;
        }

        public void setPrix(Double prix) {
            this.prix = prix;
        }

        public java.time.LocalDate getDate() {
            return date;
        }

        public void setDate(java.time.LocalDate date) {
            this.date = date;
        }
    }
}