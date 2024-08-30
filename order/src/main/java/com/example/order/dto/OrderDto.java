package com.example.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long orderId;
    private Long userId;
    private LocalDateTime createdAt;
    private String status;
    private List<BookDto> books;
    private Double totalAmount;
    private Integer totalQuantity;
}


