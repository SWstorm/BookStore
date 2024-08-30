package com.example.book.controller;

import com.example.book.model.Book;
import com.example.book.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    @GetMapping("/{title}/quantity")
    public Integer getBookQuantity(@PathVariable String title) {
        Book book = bookService.getBookByTitle(title);
        return book.getQuantity();
    }

    @PutMapping("/{title}/decrease")
    public void decreaseBookQuantity(@PathVariable String title, @RequestParam int quantity) {
        bookService.decreaseBookQuantity(title, quantity);
    }

    @PutMapping("/{id}/decrease")
    public void decreaseBookQuantityById(@PathVariable Long id, @RequestParam int quantity) {
        bookService.decreaseBookQuantityById(id, quantity);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

}
