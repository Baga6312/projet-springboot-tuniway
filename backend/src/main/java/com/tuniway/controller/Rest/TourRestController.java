package com.tuniway.controller.Rest;

import com.tuniway.model.*;
import com.tuniway.service.TourPersonnaliseService;
import com.tuniway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rest/tours")
@CrossOrigin(origins = "*")
public class TourRestController {

    @Autowired
    private TourPersonnaliseService tourService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> ajouterTour(@RequestBody TourRequest request) {
        Optional<Guide> guide = userService.getUserById(request.getGuideId())
                .filter(u -> u instanceof Guide)
                .map(u -> (Guide) u);
        if (!guide.isPresent()) {
            return new ResponseEntity<>("Guide not found", HttpStatus.NOT_FOUND);
        }

        Optional<Client> client = userService.getUserById(request.getClientId())
                .filter(u -> u instanceof Client)
                .map(u -> (Client) u);
        if (!client.isPresent()) {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
        }

        if (request.getTitre() == null || request.getTitre().trim().isEmpty()) {
            return new ResponseEntity<>("Title is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getPrix() == null || request.getPrix() < 0) {
            return new ResponseEntity<>("Valid price is required", HttpStatus.BAD_REQUEST);
        }

        TourPersonnalise tour = new TourPersonnalise();
        tour.setGuide(guide.get());
        tour.setClient(client.get());
        tour.setTitre(request.getTitre());
        tour.setDescription(request.getDescription());
        tour.setPrix(request.getPrix());
        tour.setDate(request.getDate());

        TourPersonnalise savedTour = tourService.createTour(tour);
        return new ResponseEntity<>(savedTour, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TourPersonnalise>> listTours() {
        List<TourPersonnalise> tours = tourService.getAllTours();
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPersonnalise> getTourById(@PathVariable Long id) {
        Optional<TourPersonnalise> tour = tourService.getTourById(id);
        if (tour.isPresent()) {
            return new ResponseEntity<>(tour.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTour(@PathVariable Long id, @RequestBody TourUpdateRequest request) {
        Optional<TourPersonnalise> existingTour = tourService.getTourById(id);

        if (!existingTour.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        TourPersonnalise tour = existingTour.get();

        if (request.getTitre() != null) {
            tour.setTitre(request.getTitre());
        }
        if (request.getDescription() != null) {
            tour.setDescription(request.getDescription());
        }
        if (request.getPrix() != null) {
            if (request.getPrix() < 0) {
                return new ResponseEntity<>("Price cannot be negative", HttpStatus.BAD_REQUEST);
            }
            tour.setPrix(request.getPrix());
        }
        if (request.getDate() != null) {
            tour.setDate(request.getDate());
        }

        TourPersonnalise updatedTour = tourService.updateTour(tour);
        return new ResponseEntity<>(updatedTour, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        Optional<TourPersonnalise> tour = tourService.getTourById(id);

        if (!tour.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        tourService.deleteTour(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    static class TourRequest {
        private Long guideId;
        private Long clientId;
        private String titre;
        private String description;
        private Double prix;
        private LocalDate date;

        public Long getGuideId() { return guideId; }
        public void setGuideId(Long guideId) { this.guideId = guideId; }
        public Long getClientId() { return clientId; }
        public void setClientId(Long clientId) { this.clientId = clientId; }
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrix() { return prix; }
        public void setPrix(Double prix) { this.prix = prix; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }

    static class TourUpdateRequest {
        private String titre;
        private String description;
        private Double prix;
        private LocalDate date;

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrix() { return prix; }
        public void setPrix(Double prix) { this.prix = prix; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }
}