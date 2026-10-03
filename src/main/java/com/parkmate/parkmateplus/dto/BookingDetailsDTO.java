package com.parkmate.parkmateplus.dto;

public class BookingDetailsDTO {

    private Long bookingId;

    private String status;
    private String otp;

    private Double pickupLat;
    private Double pickupLng;

    private Double parkingLat;
    private Double parkingLng;

    private String pickupLocation;
    private String parkingLocation;

    private Long userId;
    private Long vehicleId;
    private Long assistantId;

    private String userName;
    private String userEmail;
    private String userPhone;

    private String vehicleNumber;
    private String vehicleType;

    private String assistantName;
    private String assistantEmail;
    private String assistantPhone;
    private String assistantStatus;


    // =========================================================
    // BOOKING ID
    // =========================================================

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }


    // =========================================================
    // STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // =========================================================
    // OTP
    // =========================================================

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


    // =========================================================
    // PICKUP LATITUDE
    // =========================================================

    public Double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }


    // =========================================================
    // PICKUP LONGITUDE
    // =========================================================

    public Double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }


    // =========================================================
    // PARKING LATITUDE
    // =========================================================

    public Double getParkingLat() {
        return parkingLat;
    }

    public void setParkingLat(Double parkingLat) {
        this.parkingLat = parkingLat;
    }


    // =========================================================
    // PARKING LONGITUDE
    // =========================================================

    public Double getParkingLng() {
        return parkingLng;
    }

    public void setParkingLng(Double parkingLng) {
        this.parkingLng = parkingLng;
    }


    // =========================================================
    // PICKUP LOCATION
    // =========================================================

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }


    // =========================================================
    // PARKING LOCATION
    // =========================================================

    public String getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(String parkingLocation) {
        this.parkingLocation = parkingLocation;
    }


    // =========================================================
    // USER ID
    // =========================================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    // =========================================================
    // VEHICLE ID
    // =========================================================

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }


    // =========================================================
    // ASSISTANT ID
    // =========================================================

    public Long getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(Long assistantId) {
        this.assistantId = assistantId;
    }


    // =========================================================
    // USER DETAILS
    // =========================================================

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }


    // =========================================================
    // VEHICLE DETAILS
    // =========================================================

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }


    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }


    // =========================================================
    // ASSISTANT DETAILS
    // =========================================================

    public String getAssistantName() {
        return assistantName;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }


    public String getAssistantEmail() {
        return assistantEmail;
    }

    public void setAssistantEmail(String assistantEmail) {
        this.assistantEmail = assistantEmail;
    }


    public String getAssistantPhone() {
        return assistantPhone;
    }

    public void setAssistantPhone(String assistantPhone) {
        this.assistantPhone = assistantPhone;
    }


    public String getAssistantStatus() {
        return assistantStatus;
    }

    public void setAssistantStatus(String assistantStatus) {
        this.assistantStatus = assistantStatus;
    }
}