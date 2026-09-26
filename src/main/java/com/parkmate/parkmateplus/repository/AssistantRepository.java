package com.parkmate.parkmateplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parkmate.parkmateplus.entity.Assistant;

@Repository
public interface AssistantRepository extends JpaRepository<Assistant, Long> {

    List<Assistant> findAllByEmail(String email);

    Assistant findFirstByEmailOrderByIdAsc(String email);
}