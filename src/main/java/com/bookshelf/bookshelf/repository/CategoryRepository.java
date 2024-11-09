package com.bookshelf.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookshelf.bookshelf.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

