package com.tuniway.repository;

import com.tuniway.model.TourPersonnalise;
import com.tuniway.model.Guide;
import com.tuniway.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPersonnaliseRepository extends JpaRepository<TourPersonnalise, Long> {

    List<TourPersonnalise> findByGuide(Guide guide);
    List<TourPersonnalise> findByClient(Client client);
}