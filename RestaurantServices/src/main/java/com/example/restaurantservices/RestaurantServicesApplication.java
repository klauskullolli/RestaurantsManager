package com.example.restaurantservices;

import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@Slf4j
public class RestaurantServicesApplication {

    @Autowired
    UserRepository userRepository;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public static void main(String[] args) {
        SpringApplication.run(RestaurantServicesApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public void defaultSuperUser() {
        List<User> superUsers = userRepository.findAllByRole(Role.SUPERUSER);
        if (superUsers.size() == 0) {
            User superUser = new User();
            superUser.setUsername("superuser");
            superUser.setPassword(passwordEncoder.encode("1234"));
            superUser.setRole(Role.SUPERUSER);
            superUser = userRepository.save(superUser);
            log.info(String.format("This is the default superuser : " + superUser));
        }
    }

}
