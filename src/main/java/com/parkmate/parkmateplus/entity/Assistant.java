package com.parkmate.parkmateplus.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "assistants")
public class Assistant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String status;

    private Double rating;

    // =========================================================
    // ASSISTANT LOCATION
    // =========================================================

    private Double latitude;

    private Double longitude;

    private LocalDateTime lastLocationUpdate;

    // =========================================================
    // PASSWORD
    // =========================================================

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Assistant() {
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Assistant(
            Long id,
            String name,
            String email,
            String phone,
            String status,
            Double rating) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.rating = rating;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public Double getRating() {
        return rating;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public String getPassword() {
        return password;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLastLocationUpdate(
            LocalDateTime lastLocationUpdate) {

        this.lastLocationUpdate =
                lastLocationUpdate;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}