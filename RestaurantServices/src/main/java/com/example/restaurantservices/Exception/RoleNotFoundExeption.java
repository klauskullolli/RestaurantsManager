package com.example.restaurantservices.Exception;

public class RoleNotFoundExeption extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public RoleNotFoundExeption(String message) {
        super(message);
    }
}
