package com.tuniway.repository;

import com.tuniway.model.Reservation;
import com.tuniway.model.Client;
import com.tuniway.model.Guide;
import com.tuniway.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByClient(Client client);
    List<Reservation> findByGuide(Guide guide);
    List<Reservation> findByStatus(ReservationStatus status);
}