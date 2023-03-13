package com.example.restaurantservices.Controller;

import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Exception.NoAuthorizationException;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.UserRepository;
import com.example.restaurantservices.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User with id: " + id + " does not exist");
        } else {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAnyAuthority('SUPERUSER')")
    public ResponseEntity<User> createAdmin(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ADMIN);
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
    }

    @PostMapping("/client")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<User> createClinet(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CLIENT);
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
    }


    @PostMapping("/manager/restaurant/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<User> createManager(@PathVariable Integer id, @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return new ResponseEntity<>(userService.addManager(id, user), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(id);

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User loggedUser = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User with id: " + id + " does not exist");
        } else {
            User newUser = userOptional.get();
            if (loggedUser.getRole().equals(Role.ADMIN) && newUser.getRole().equals(Role.ADMIN)) {
                throw new NoAuthorizationException("This user is not authorized");
            }
            newUser.setName(user.getName());
            newUser.setSurname(user.getSurname());
            newUser.setUsername(user.getUsername());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser.setPhone(user.getPhone());
            return new ResponseEntity<>(userRepository.save(newUser), HttpStatus.OK);
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<User> deleteUser(@PathVariable Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User with Id: " + id + " does not exist");
        } else {
            userRepository.deleteById(id);
            return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
        }

    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String role) {
        return new ResponseEntity<>(userService.getUsers(role), HttpStatus.OK);
    }

    @GetMapping("username/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER')")
    public User getUserUsename(@PathVariable String username) {

        return userRepository.findByUsername(username);
    }


}
