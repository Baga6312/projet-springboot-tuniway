package com.tuniway.service;

import com.tuniway.model.TourPersonnalise;
import com.tuniway.model.Guide;
import com.tuniway.model.Client;
import com.tuniway.repository.TourPersonnaliseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TourPersonnaliseService {

    @Autowired
    private TourPersonnaliseRepository tourPersonnaliseRepository;

    public List<TourPersonnalise> getAllTours() {
        return tourPersonnaliseRepository.findAll();
    }

    public Optional<TourPersonnalise> getTourById(Long id) {
        return tourPersonnaliseRepository.findById(id);
    }

    public List<TourPersonnalise> getToursByGuide(Guide guide) {
        return tourPersonnaliseRepository.findByGuide(guide);
    }

    public List<TourPersonnalise> getToursByClient(Client client) {
        return tourPersonnaliseRepository.findByClient(client);
    }

    public TourPersonnalise createTour(TourPersonnalise tour) {
        return tourPersonnaliseRepository.save(tour);
    }

    public TourPersonnalise updateTour(TourPersonnalise tour) {
        return tourPersonnaliseRepository.save(tour);
    }

    public void deleteTour(Long id) {
        tourPersonnaliseRepository.deleteById(id);
    }
}