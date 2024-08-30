package com.example.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {
    private Long bookId;
    private String title;
    private Integer quantity;
    private Double price;
}
