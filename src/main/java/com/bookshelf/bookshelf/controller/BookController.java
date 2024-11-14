package com.bookshelf.bookshelf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bookshelf.bookshelf.model.Book;
import com.bookshelf.bookshelf.repository.BookRepository;
import com.bookshelf.bookshelf.service.BookService;

import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping("/search")
    public Mono<String> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int startIndex,
            @RequestParam(defaultValue = "10") int maxResults) {
        return bookService.searchBooks(query, startIndex, maxResults);
    }

    @GetMapping("/bestsellers")
    public Mono<String> getBestsellers(@RequestParam(defaultValue = "10") int maxResults) {
        return bookService.searchBestsellers(maxResults);
    }

    @GetMapping("/details/{bookId}")
    public Mono<String> getBookDetails(@PathVariable String bookId) {
        return bookService.getBookDetails(bookId);
    }
}
