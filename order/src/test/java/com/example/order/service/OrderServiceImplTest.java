package com.example.order.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.order.dto.OrderDto;
import com.example.order.model.BookResponse;
import com.example.order.model.Order;
import com.example.order.model.OrderBook;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderBook orderBook;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
        orderBook.setBookId(1L);
        orderBook.setQuantity(2);

        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CREATED");
        order.setBooks(Arrays.asList(orderBook));

        bookResponse = new BookResponse();
        bookResponse.setId(1L);
        bookResponse.setTitle("Effective Java");
        bookResponse.setPrice(45.0);
        bookResponse.setQuantity(10);
    }

    @Test
    void createOrder_shouldCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(restTemplate.getForObject(anyString(), eq(BookResponse.class))).thenReturn(bookResponse);

        Order createdOrder = orderService.createOrder(1L, Arrays.asList(orderBook));

        assertNotNull(createdOrder);
        assertEquals("CREATED", createdOrder.getStatus());
        assertEquals(1L, createdOrder.getUserId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BookResponse.class));
    }

    @Test
    void createOrder_shouldThrowExceptionIfBookNotAvailable() {
        // Мокаем restTemplate для возвращения null, чтобы вызвать исключение
        when(restTemplate.getForObject(anyString(), eq(BookResponse.class))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L, Arrays.asList(orderBook)));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersByUser_shouldReturnOrders() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));

        List<Order> orders = orderService.getOrdersByUser(1L);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order.getUserId(), orders.get(0).getUserId());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getOrderById_shouldReturnOrderDto() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restTemplate.getForObject(anyString(), eq(BookResponse.class))).thenReturn(bookResponse);

        OrderDto orderDto = orderService.getOrderById(1L);

        assertNotNull(orderDto);
        assertEquals(1L, orderDto.getOrderId());
        assertEquals(1L, orderDto.getUserId());
        assertEquals("CREATED", orderDto.getStatus());
        assertEquals(2, orderDto.getTotalQuantity());
        assertEquals(90.0, orderDto.getTotalAmount());
        verify(orderRepository, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BookResponse.class));
    }

    @Test
    void getOrderById_shouldThrowExceptionIfOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderDetails_shouldReturnOrderDto() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restTemplate.getForObject(anyString(), eq(BookResponse.class))).thenReturn(bookResponse);

        OrderDto orderDto = orderService.getOrderDetails(1L);

        assertNotNull(orderDto);
        assertEquals(1L, orderDto.getOrderId());
        assertEquals(1L, orderDto.getUserId());
        assertEquals("CREATED", orderDto.getStatus());
        assertEquals(2, orderDto.getTotalQuantity());
        assertEquals(90.0, orderDto.getTotalAmount());
        verify(orderRepository, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BookResponse.class));
    }

    @Test
    void getOrderDetails_shouldHandleMissingBookResponse() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restTemplate.getForObject(anyString(), eq(BookResponse.class))).thenReturn(null);

        OrderDto orderDto = orderService.getOrderDetails(1L);

        assertNotNull(orderDto);
        assertEquals(1L, orderDto.getOrderId());
        assertEquals(1L, orderDto.getUserId());
        assertEquals("Unknown", orderDto.getBooks().get(0).getTitle());
        assertEquals(0.0, orderDto.getBooks().get(0).getPrice());
        verify(orderRepository, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BookResponse.class));
    }
}