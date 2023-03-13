package com.example.restaurantservices.Service;

import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Exception.NoAuthorizationException;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Exception.RoleNotFoundExeption;
import com.example.restaurantservices.Model.Restaurant;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.RestaurantRepository;
import com.example.restaurantservices.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public User addManager(Integer restaurantId, User restaurantManager) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant with id: " + restaurantId + " does not exist");
        } else {
            restaurantManager.setRestaurant(restaurantOptional.get());
            restaurantManager.setRole(Role.RESTAURANT_MANAGER);
            return userRepository.save(restaurantManager);
        }

    }


    public User getCurrentLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User loggedUser = userRepository.findByUsername(username);
        return loggedUser;
    }

    public List<User> getUsers(String role) {
        
        User loggedUser = getCurrentLoggedUser();
        if (loggedUser.getRole().equals(Role.ADMIN)) {
            if (role != null && !role.isEmpty()) {
                role = role.toLowerCase(Locale.ROOT);
                if (role.equals(Role.ADMIN.name().toLowerCase(Locale.ROOT))) {
                    throw new NoAuthorizationException("This user has no authorization");
                }
                if (role.equals(Role.CLIENT.name().toLowerCase(Locale.ROOT))) {
                    return userRepository.findAllByRole(Role.CLIENT);
                }
                if (role.equals(Role.RESTAURANT_MANAGER.name().toLowerCase(Locale.ROOT))) {
                    return userRepository.findAllByRole(Role.RESTAURANT_MANAGER);
                } else {
                    throw new RoleNotFoundExeption("This role: " + role + " does not exits");
                }
            } else {
                List<User> users = userRepository.findAllByRole(Role.CLIENT);
                users.addAll(userRepository.findAllByRole(Role.RESTAURANT_MANAGER));
                users.add(loggedUser);
                return users;
            }
        } else {
            if (role != null && !role.isEmpty()) {
                role = role.toLowerCase(Locale.ROOT);
                if (role.equals(Role.ADMIN.name().toLowerCase(Locale.ROOT))) {
                    return userRepository.findAllByRole(Role.ADMIN);
                }
                if (role.equals(Role.CLIENT.name().toLowerCase(Locale.ROOT))) {
                    return userRepository.findAllByRole(Role.CLIENT);
                }
                if (role.equals(Role.RESTAURANT_MANAGER.name().toLowerCase(Locale.ROOT))) {
                    return userRepository.findAllByRole(Role.RESTAURANT_MANAGER);
                } else {
                    throw new RoleNotFoundExeption("This role: " + role + " does not exits");
                }
            } else {
                return userRepository.findAll();
            }
        }


    }


}
