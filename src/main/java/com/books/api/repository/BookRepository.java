package com.books.api.repository;

import com.books.api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findBytitle(String title);
    List<Book> findAllByOrderByTitleAsc();
}