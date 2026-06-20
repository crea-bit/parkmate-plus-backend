package com.parkmate.parkmateplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkmate.parkmateplus.entity.Assistant;

public interface AssistantRepository
        extends JpaRepository<Assistant, Long> {

    Assistant findByEmail(String email);
}