package com.cinema.model;

import java.math.BigDecimal;

public class BookingSeat {
    private int bookingSeatId;
    private int bookingId;
    private int showId;
    private String seatNumber;
    private BigDecimal seatPrice;
    private String seatType;

    public BookingSeat() {}

    public BookingSeat(int bookingSeatId, int bookingId, int showId, String seatNumber, BigDecimal seatPrice, String seatType) {
        this.bookingSeatId = bookingSeatId;
        this.bookingId = bookingId;
        this.showId = showId;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;
        this.seatType = seatType;
    }

    public int getBookingSeatId() { return bookingSeatId; }
    public void setBookingSeatId(int bookingSeatId) { this.bookingSeatId = bookingSeatId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public BigDecimal getSeatPrice() { return seatPrice; }
    public void setSeatPrice(BigDecimal seatPrice) { this.seatPrice = seatPrice; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
}
