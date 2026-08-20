package com.codewithhasita.webrelayretryservice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DBConnector extends JpaRepository<Event, Long> {
}
