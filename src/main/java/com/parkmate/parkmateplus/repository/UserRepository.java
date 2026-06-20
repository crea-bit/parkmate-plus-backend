package com.parkmate.parkmateplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkmate.parkmateplus.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}