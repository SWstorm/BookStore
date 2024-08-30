package com.example.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponse {
    private Long id;
    private String title;
    private int quantity;
    private Double price;

}