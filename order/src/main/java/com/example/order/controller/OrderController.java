package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.model.CreateOrderRequest;
import com.example.order.model.Order;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public OrderDto createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getUserId(), request.getBooks());
        return orderService.getOrderDetails(order.getId());
    }

    @GetMapping("/user/{userId}")
    public List<OrderDto> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return orders.stream().map(order -> orderService.getOrderDetails(order.getId())).collect(Collectors.toList());
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId);
    }
}