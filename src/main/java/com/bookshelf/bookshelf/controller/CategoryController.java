package com.bookshelf.bookshelf.controller;

import org.springframework.web.bind.annotation.*;

import com.bookshelf.bookshelf.model.Category;
import com.bookshelf.bookshelf.repository.CategoryRepository;
import com.bookshelf.bookshelf.service.BookService;

import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final BookService bookService;

    public CategoryController(CategoryRepository categoryRepository, BookService bookService) {
        this.categoryRepository = categoryRepository;
        this.bookService = bookService;
    }

    @GetMapping("/")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/books")
    public Mono<String> getBooksByCategories(@RequestParam(defaultValue = "10") int maxResults) {
        List<Category> categories = categoryRepository.findAll();
        return bookService.searchBooksByCategory(categories, maxResults);
    }
    
}