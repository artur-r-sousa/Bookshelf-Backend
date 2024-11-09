package com.bookshelf.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookshelf.bookshelf.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}