package com.parkmate.parkmateplus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_locations")
public class ParkingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private Integer totalSpaces;

    private Integer occupiedSpaces;

    private Integer availableSpaces;

    private String status;

    public ParkingLocation() {
    }

    public ParkingLocation(
            String name,
            Double latitude,
            Double longitude,
            Integer totalSpaces,
            Integer occupiedSpaces) {

        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalSpaces = totalSpaces;
        this.occupiedSpaces = occupiedSpaces;
        this.availableSpaces =
                totalSpaces - occupiedSpaces;

        updateStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getTotalSpaces() {
        return totalSpaces;
    }

    public void setTotalSpaces(Integer totalSpaces) {
        this.totalSpaces = totalSpaces;
        updateAvailability();
    }

    public Integer getOccupiedSpaces() {
        return occupiedSpaces;
    }

    public void setOccupiedSpaces(Integer occupiedSpaces) {
        this.occupiedSpaces = occupiedSpaces;
        updateAvailability();
    }

    public Integer getAvailableSpaces() {
        return availableSpaces;
    }

    public void setAvailableSpaces(Integer availableSpaces) {
        this.availableSpaces = availableSpaces;
        updateStatus();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private void updateAvailability() {

        if (totalSpaces != null &&
                occupiedSpaces != null) {

            availableSpaces =
                    Math.max(
                            totalSpaces - occupiedSpaces,
                            0
                    );

            updateStatus();
        }
    }

    private void updateStatus() {

        if (availableSpaces == null) {
            status = "UNKNOWN";
        }
        else if (availableSpaces <= 0) {
            status = "FULL";
        }
        else {
            status = "AVAILABLE";
        }
    }
}