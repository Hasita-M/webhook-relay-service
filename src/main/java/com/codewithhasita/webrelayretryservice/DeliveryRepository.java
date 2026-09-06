package com.codewithhasita.webrelayretryservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByEventOrderByAttemptNumberAsc(Event event);
}
