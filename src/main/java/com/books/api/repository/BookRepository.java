package com.books.api.repository;

import com.books.api.model.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);
    List<Book> findByStatus(Book.Status status);

    List<Book> findByStatus(Book.Status status, Sort sort);

    List<Book> findAllByOrderByTitleAsc();
}