package com.example.order.model;

import com.example.order.model.OrderBook;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private Long userId;
    private List<OrderBook> books;
}