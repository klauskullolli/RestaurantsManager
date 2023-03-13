package com.example.restaurantservices.Exception;

public class ProductExistExeption extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public ProductExistExeption(String message) {
        super(message);
    }
}
