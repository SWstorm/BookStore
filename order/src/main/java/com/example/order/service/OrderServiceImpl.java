package com.example.order.service;

import com.example.order.dto.BookDto;
import com.example.order.dto.OrderDto;
import com.example.order.model.BookResponse;
import com.example.order.model.Order;
import com.example.order.model.OrderBook;
import com.example.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Order createOrder(Long userId, List<OrderBook> books) {
        if (!isUserValid(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        for (OrderBook orderBook : books) {
            if (!isBookAvailable(orderBook.getBookId(), orderBook.getQuantity())) {
                throw new IllegalArgumentException("Book not available: " + orderBook.getBookId());
            }
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CREATED");
        order.setBooks(books);

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return getOrderDetails(orderId);
    }

    @Override
    public OrderDto getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        List<BookDto> bookDtos = order.getBooks().stream().map(orderBook -> {
            String url = "http://book-service/books/" + orderBook.getBookId();
            BookResponse bookResponse = restTemplate.getForObject(url, BookResponse.class);

            BookDto bookDto = new BookDto();
            bookDto.setBookId(orderBook.getBookId());
            bookDto.setQuantity(orderBook.getQuantity());

            if (bookResponse != null) {
                bookDto.setTitle(bookResponse.getTitle());
                bookDto.setPrice(bookResponse.getPrice());
            } else {
                bookDto.setTitle("Unknown");
                bookDto.setPrice(0.0);
            }

            return bookDto;
        }).collect(Collectors.toList());

        double totalAmount = bookDtos.stream()
                .mapToDouble(bookDto -> bookDto.getPrice() * bookDto.getQuantity())
                .sum();
        int totalQuantity = bookDtos.stream()
                .mapToInt(BookDto::getQuantity)
                .sum();

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getId());
        orderDto.setUserId(order.getUserId());
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setStatus(order.getStatus());
        orderDto.setBooks(bookDtos);
        orderDto.setTotalAmount(totalAmount);
        orderDto.setTotalQuantity(totalQuantity);

        return orderDto;
    }

    boolean isUserValid(Long userId) {
        return true;
    }

    private boolean isBookAvailable(Long bookId, int quantity) {
        String url = "http://book-service/books/" + bookId;
        BookResponse bookResponse = restTemplate.getForObject(url, BookResponse.class);
        return bookResponse != null && bookResponse.getQuantity() >= quantity;
    }
}
