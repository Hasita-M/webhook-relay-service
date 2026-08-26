package com.codewithhasita.webrelayretryservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e JOIN FETCH e.receiver WHERE e.id = :id")
    Optional<Event> findByIdWithReceiver(@Param("id") Long id);
}
