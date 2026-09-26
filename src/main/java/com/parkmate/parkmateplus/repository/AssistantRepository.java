package com.parkmate.parkmateplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkmate.parkmateplus.entity.Assistant;

public interface AssistantRepository extends JpaRepository<Assistant, Long> {

    Assistant findByEmail(String email);

    List<Assistant> findAllByEmail(String email);
}