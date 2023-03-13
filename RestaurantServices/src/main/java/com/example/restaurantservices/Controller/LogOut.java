package com.example.restaurantservices.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log-out")
public class LogOut {

    public static Boolean LOGG_IN = false;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> logout() {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        SecurityContextHolder.getContext().setAuthentication(null);
        auth.setAuthenticated(false);
        LOGG_IN = false;

        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }
}
