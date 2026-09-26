package com.parkmate.parkmateplus.dto;

public class AssistantNearbyDTO {

    private Long id;

    private String name;

    private String phone;

    private Double latitude;

    private Double longitude;

    private String status;

    private Double rating;

    private Double distanceKm;


    public AssistantNearbyDTO() {
    }


    public AssistantNearbyDTO(
            Long id,
            String name,
            String phone,
            Double latitude,
            Double longitude,
            String status,
            Double rating,
            Double distanceKm) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.rating = rating;
        this.distanceKm = distanceKm;
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


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
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


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public Double getRating() {
        return rating;
    }


    public void setRating(Double rating) {
        this.rating = rating;
    }


    public Double getDistanceKm() {
        return distanceKm;
    }


    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
}