package com.example.restaurantservices.Exception;

public class TimeError extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public TimeError(String message) {
        super(message);
    }
}
