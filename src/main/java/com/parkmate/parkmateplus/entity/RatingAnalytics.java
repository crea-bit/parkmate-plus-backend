package com.parkmate.parkmateplus.entity;

public class RatingAnalytics {

    private Long assistantId;
    private double averageRating;
    private long totalRatings;

    public RatingAnalytics() {
    }

    public RatingAnalytics(Long assistantId, double averageRating, long totalRatings) {
        this.assistantId = assistantId;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
    }

    public Long getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(Long assistantId) {
        this.assistantId = assistantId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }
}