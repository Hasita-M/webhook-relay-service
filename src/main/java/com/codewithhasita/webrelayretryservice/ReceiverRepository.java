package com.codewithhasita.webrelayretryservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    Optional<Receiver> findByManagementToken(String managementToken);
}
