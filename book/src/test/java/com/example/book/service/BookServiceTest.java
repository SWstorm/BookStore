package com.example.book.service;

import com.example.book.model.Book;
import com.example.book.repositrory.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setDescription("A comprehensive guide to programming in Java.");
        book.setPrice(45.0);
        book.setQuantity(10);
    }

    @Test
    void createBook_shouldSaveBook() {
        when(bookRepository.save(book)).thenReturn(book);

        Book createdBook = bookService.createBook(book);

        assertNotNull(createdBook);
        assertEquals(book.getTitle(), createdBook.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void getBookByTitle_shouldReturnBook() {
        when(bookRepository.findByTitle("Effective Java")).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookByTitle("Effective Java");

        assertNotNull(foundBook);
        assertEquals(book.getTitle(), foundBook.getTitle());
        verify(bookRepository, times(1)).findByTitle("Effective Java");
    }

    @Test
    void getBookByTitle_shouldThrowExceptionIfNotFound() {
        when(bookRepository.findByTitle("Nonexistent Book")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookService.getBookByTitle("Nonexistent Book"));
        verify(bookRepository, times(1)).findByTitle("Nonexistent Book");
    }

    @Test
    void getAllBooks_shouldReturnAllBooks() {
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> allBooks = bookService.getAllBooks();

        assertNotNull(allBooks);
        assertEquals(1, allBooks.size());
        assertEquals(book.getTitle(), allBooks.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void decreaseBookQuantity_shouldDecreaseQuantity() {
        when(bookRepository.findByTitle("Effective Java")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.decreaseBookQuantity("Effective Java", 5);

        assertEquals(5, book.getQuantity());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void decreaseBookQuantity_shouldThrowExceptionIfNotEnoughStock() {
        when(bookRepository.findByTitle("Effective Java")).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> bookService.decreaseBookQuantity("Effective Java", 15));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void decreaseBookQuantityById_shouldDecreaseQuantity() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.decreaseBookQuantityById(1L, 5);

        assertEquals(5, book.getQuantity());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void decreaseBookQuantityById_shouldThrowExceptionIfNotEnoughStock() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> bookService.decreaseBookQuantityById(1L, 15));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getBookById_shouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getBookById(1L);

        assertTrue(foundBook.isPresent());
        assertEquals(book.getId(), foundBook.get().getId());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_shouldReturnEmptyIfNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.getBookById(1L);

        assertFalse(foundBook.isPresent());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void updateBook_shouldUpdateAndReturnBook() {
        Book updatedDetails = new Book();
        updatedDetails.setTitle("Clean Code");
        updatedDetails.setAuthor("Robert Martin");
        updatedDetails.setDescription("A handbook of agile software craftsmanship.");
        updatedDetails.setPrice(50.0);
        updatedDetails.setQuantity(20);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedDetails);

        Book updatedBook = bookService.updateBook(1L, updatedDetails);

        assertNotNull(updatedBook);
        assertEquals("Clean Code", updatedBook.getTitle());
        assertEquals("Robert Martin", updatedBook.getAuthor());
        assertEquals("A handbook of agile software craftsmanship.", updatedBook.getDescription());
        assertEquals(50.0, updatedBook.getPrice());
        assertEquals(20, updatedBook.getQuantity());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBook_shouldThrowExceptionIfBookNotFound() {
        Book updatedDetails = new Book();
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(1L, updatedDetails));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_shouldDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }
}