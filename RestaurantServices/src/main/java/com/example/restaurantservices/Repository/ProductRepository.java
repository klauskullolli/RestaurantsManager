package com.example.restaurantservices.Repository;

import com.example.restaurantservices.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
