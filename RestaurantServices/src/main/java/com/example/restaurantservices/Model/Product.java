package com.example.restaurantservices.Model;

import com.example.restaurantservices.Enum.Belonging;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "Prduct")

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private double price;

    private int amount;


    @NotNull(message = "Product belonging can not be null")
    private Belonging belonging;

    private String description;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_menu",

            joinColumns =
                    {@JoinColumn(name = "menu_id")},
            inverseJoinColumns =
                    {@JoinColumn(name = "product_id")})
    @JsonIgnore
    private List<Menu> menus;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Sell> sells = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", belonging=" + belonging +
                ", description='" + description + '\'' +
                ", menus=" + menus +
                ", sells=" + sells +
                '}';
    }
}
