package com.example.restaurantservices.Repository;

import com.example.restaurantservices.Model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
}
