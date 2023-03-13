package com.example.restaurantservices.Repository;

import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.role = ?1")
    List<User> findAllByRole(Role role);

    User findByUsername(String username);
}
