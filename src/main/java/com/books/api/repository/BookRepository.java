package com.books.api.repository;

import com.books.api.model.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByStatus(Book.Status status);

    List<Book> findByStatus(Book.Status status, Sort sort);
}