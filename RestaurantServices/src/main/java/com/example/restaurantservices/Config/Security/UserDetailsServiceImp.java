package com.example.restaurantservices.Config.Security;

import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private Logger logger = LogManager.getLogger(UserDetailsServiceImp.class);

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("User not found ");
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else {
            logger.info(String.format("User found with username: %s", username));
            Collection<SimpleGrantedAuthority> authorityCollections = new ArrayList<>();
            authorityCollections.add(new SimpleGrantedAuthority(user.getRole().name()));
            return UserDetailsImpl.build(user);
        }
    }

}
