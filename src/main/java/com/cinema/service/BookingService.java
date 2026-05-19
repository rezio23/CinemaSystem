package com.cinema.service;

import com.cinema.dao.BookingDao;
import com.cinema.dao.BookingSeatDao;
import com.cinema.model.Booking;
import com.cinema.model.BookingSeat;
import com.cinema.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookingService {

    private final BookingDao bookingDao = new BookingDao();
    private final BookingSeatDao bookingSeatDao = new BookingSeatDao();

    public int createBooking(Booking booking, List<BookingSeat> seats) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Re-check taken seats inside transaction to prevent race conditions
            List<String> taken = bookingSeatDao.getTakenSeatNumbersByShow(conn, booking.getShowId());
            for (BookingSeat bs : seats) {
                if (taken.contains(bs.getSeatNumber())) {
                    throw new CinemaException("Seat " + bs.getSeatNumber() + " was just booked by someone else. Please choose different seats.");
                }
            }

            int bookingId = bookingDao.insert(conn, booking);
            if (bookingId <= 0) {
                throw new CinemaException("Failed to create booking record.");
            }

            for (BookingSeat bs : seats) {
                bs.setBookingId(bookingId);
                bookingSeatDao.insert(conn, bs);
            }

            conn.commit();
            return bookingId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            String msg = e.getMessage();
            if (msg != null && (msg.contains("UQ_SHOW_SEAT") || msg.contains("ORA-00001"))) {
                throw new CinemaException("One or more seats were already booked. Please refresh and choose different seats.");
            }
            throw new CinemaException("Booking transaction failed: " + e.getMessage(), e);
        } catch (CinemaException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public void cancelBooking(int bookingId) {
        bookingDao.updateStatus(bookingId, "CANCELLED");
    }
}
