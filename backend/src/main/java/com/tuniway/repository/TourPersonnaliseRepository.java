package com.tuniway.repository;

import com.tuniway.model.TourPersonnalise;
import com.tuniway.model.Guide;
import com.tuniway.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPersonnaliseRepository extends JpaRepository<TourPersonnalise, Long> {

    List<TourPersonnalise> findByGuide(Guide guide);
    List<TourPersonnalise> findByClient(Client client);

    // ✅ ADD THIS METHOD - Query by guide ID directly
    @Query("SELECT t FROM TourPersonnalise t WHERE t.guide.id = :guideId")
    List<TourPersonnalise> findByGuideId(@Param("guideId") Long guideId);

    // ✅ ADD THIS METHOD - Query by client ID directly
    @Query("SELECT t FROM TourPersonnalise t WHERE t.client.id = :clientId")
    List<TourPersonnalise> findByClientId(@Param("clientId") Long clientId);
}