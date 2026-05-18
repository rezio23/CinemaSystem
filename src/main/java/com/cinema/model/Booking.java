package com.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int customerId;
    private int showId;
    private int staffId;
    private LocalDateTime bookingDate;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String status;

    // Joined display fields
    private String customerName;
    private String movieTitle;
    private String hallName;
    private LocalDateTime showDateTime;
    private String staffName;

    public Booking() {}

    public Booking(int bookingId, int customerId, int showId, int staffId, LocalDateTime bookingDate,
                   String paymentMethod, BigDecimal totalAmount, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.showId = showId;
        this.staffId = staffId;
        this.bookingDate = bookingDate;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public LocalDateTime getShowDateTime() { return showDateTime; }
    public void setShowDateTime(LocalDateTime showDateTime) { this.showDateTime = showDateTime; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
}
