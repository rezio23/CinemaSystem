package com.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovieShow {
    private int showId;
    private int movieId;
    private int hallId;
    private LocalDateTime showDateTime;
    private BigDecimal basePrice;

    // Joined fields for display
    private String movieTitle;
    private String hallName;
    private int hallCapacity;

    public MovieShow() {}

    public MovieShow(int showId, int movieId, int hallId, LocalDateTime showDateTime, BigDecimal basePrice) {
        this.showId = showId;
        this.movieId = movieId;
        this.hallId = hallId;
        this.showDateTime = showDateTime;
        this.basePrice = basePrice;
    }

    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getHallId() { return hallId; }
    public void setHallId(int hallId) { this.hallId = hallId; }

    public LocalDateTime getShowDateTime() { return showDateTime; }
    public void setShowDateTime(LocalDateTime showDateTime) { this.showDateTime = showDateTime; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public int getHallCapacity() { return hallCapacity; }
    public void setHallCapacity(int hallCapacity) { this.hallCapacity = hallCapacity; }

    @Override
    public String toString() {
        return movieTitle + " @ " + hallName + " - " + showDateTime;
    }
}
