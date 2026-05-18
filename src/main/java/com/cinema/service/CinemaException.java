package com.cinema.service;

public class CinemaException extends RuntimeException {
    public CinemaException(String message) {
        super(message);
    }
    public CinemaException(String message, Throwable cause) {
        super(message, cause);
    }
}
