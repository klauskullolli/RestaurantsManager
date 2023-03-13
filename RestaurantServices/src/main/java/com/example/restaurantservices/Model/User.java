package com.example.restaurantservices.Model;

import com.example.restaurantservices.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Table(name = "User", indexes = {
        @Index(name = "idx_user_username", columnList = "username")
})
@Entity
@Getter
@Setter
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;


    private String surname;

    private String email;

    private int phone;

    @Column(unique = true)
    @NotBlank(message = "Username can't be null")
    @Size(max = 128)
    private String username;

    @NotBlank(message = "Password can't be null")
    private String password;

    private Role role;


    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "user_restaurant",
            joinColumns =
                    {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns =
                    {@JoinColumn(name = "user_id")})
    @JsonIgnore
    private Restaurant restaurant;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone=" + phone +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", restaurant=" + restaurant +
                ", orders=" + orders +
                '}';
    }
}
