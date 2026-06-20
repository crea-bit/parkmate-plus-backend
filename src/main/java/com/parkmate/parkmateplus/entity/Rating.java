package com.parkmate.parkmateplus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private Long userId;
    private Long assistantId;

    private Integer rating;
    private String feedback;

    public Rating() {
    }

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAssistantId() {
        return assistantId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAssistantId(Long assistantId) {
        this.assistantId = assistantId;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}