package com.example.restaurantservices.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "Sell")

public class Sell {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id ;

    @NotNull(message = "Amount can't be null")
    private  Integer amount ;

    private Double price ;

    private String descripion ;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;


    @ManyToOne(fetch =FetchType.LAZY,  optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;
}
