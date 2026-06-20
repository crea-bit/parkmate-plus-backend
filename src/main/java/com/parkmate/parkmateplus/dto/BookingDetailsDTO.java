package com.parkmate.parkmateplus.dto;

public class BookingDetailsDTO {

    private Long id;
    private Long userId;
    private String userName;

    private Long vehicleId;
    private String vehicleNumber;
    private String vehicleType;

    private Long assistantId;
    private String assistantName;

    private String pickupLocation;
    private String parkingLocation;
    private String status;
    private String otp;

    private Double pickupLat;
    private Double pickupLng;
    private Double parkingLat;
    private Double parkingLng;

    public BookingDetailsDTO() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public Long getAssistantId() {
        return assistantId;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getParkingLocation() {
        return parkingLocation;
    }

    public String getStatus() {
        return status;
    }

    public String getOtp() {
        return otp;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public Double getParkingLat() {
        return parkingLat;
    }

    public Double getParkingLng() {
        return parkingLng;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setAssistantId(Long assistantId) {
        this.assistantId = assistantId;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public void setParkingLocation(String parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public void setParkingLat(Double parkingLat) {
        this.parkingLat = parkingLat;
    }

    public void setParkingLng(Double parkingLng) {
        this.parkingLng = parkingLng;
    }
}