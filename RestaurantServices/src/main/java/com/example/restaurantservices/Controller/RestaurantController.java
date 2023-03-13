package com.example.restaurantservices.Controller;

import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Model.Restaurant;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.RestaurantRepository;
import com.example.restaurantservices.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    RestaurantRepository restaurantRepository;


    @Autowired
    UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        return new ResponseEntity<>(restaurantRepository.save(restaurant), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Integer id, @RequestBody Restaurant restaurant) {
        Optional<Restaurant> res = restaurantRepository.findById(id);
        if (res.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant with id: " + id + " does not exist");
        } else {
            Restaurant newRestaurant = res.get();
            newRestaurant.setName(restaurant.getName());
            newRestaurant.setAddress(restaurant.getAddress());
            newRestaurant.setRate(restaurant.getRate());
            return new ResponseEntity<>(createRestaurant(newRestaurant).getBody(), HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER', 'CLIENT')")
    public ResponseEntity<Restaurant> deleteRestaurant(@PathVariable Integer id) {
        Optional<Restaurant> prod = restaurantRepository.findById(id);
        if (prod.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant with Id: " + id + " does not exist");
        } else {
            restaurantRepository.deleteById(id);
            return new ResponseEntity<>(prod.get(), HttpStatus.OK);
        }

    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER', 'CLIENT')")
    public List<Restaurant> getRestaurants() {
        return restaurantRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable Integer id) {
        Optional<Restaurant> rest = restaurantRepository.findById(id);
        if (rest.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant with id: " + id + " does not exist");
        } else {
            return new ResponseEntity<>(rest.get(), HttpStatus.OK);
        }
    }


    @GetMapping("{id}/manager")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<User> getRestaurantManager(@PathVariable Integer id) {
        Optional<Restaurant> rest = restaurantRepository.findById(id);
        if (rest.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant with id: " + id + " does not exist");
        } else {
            return new ResponseEntity<>(rest.get().getRestaurantManager(), HttpStatus.OK);
        }
    }


    @PostMapping("{id}/add/manager/{managerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<Restaurant> addManager(@PathVariable Integer id, @PathVariable Integer managerId) {
        Optional<User> userOptional = userRepository.findById(managerId);
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant with id: " + id + " does not exist");
        }
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User with id: " + id + " does not exist");
        } else {
            User restaurantManager = userOptional.get();
            Restaurant restaurant = restaurantOptional.get();

            restaurantManager.setRole(Role.RESTAURANT_MANAGER);

            restaurant.setRestaurantManager(restaurantManager);

            return new ResponseEntity<>(restaurantRepository.save(restaurant), HttpStatus.OK);

        }

    }


}
