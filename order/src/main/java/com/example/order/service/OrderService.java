package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.model.Order;
import com.example.order.model.OrderBook;

import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, List<OrderBook> books);
    List<Order> getOrdersByUser(Long userId);
    OrderDto getOrderById(Long orderId);
    OrderDto getOrderDetails(Long orderId);
}