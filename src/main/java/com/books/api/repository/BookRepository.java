package com.books.api.repository;

import com.books.api.model.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByStatus(Book.Status status);
    List<Book> findByStatus(Book.Status status, Sort sort);

    /**
     * Pesquisa livros ativos (status ON) onde o termo de pesquisa
     * é encontrado no título, autor, sinopse ou palavras-chave.
     * A pesquisa é case-insensitive.
     *
     * @param status O status do livro a ser pesquisado (deve ser Book.Status.ON).
     * @param searchTerm O termo a ser pesquisado nos campos.
     * @return Uma lista de livros que correspondem aos critérios.
     */
    @Query("SELECT b FROM Book b WHERE b.status = :status " +
            "AND (LOWER(b.title) LIKE CONCAT('%', LOWER(:searchTerm), '%') " +
            "OR LOWER(b.author) LIKE CONCAT('%', LOWER(:searchTerm), '%') " +
            "OR LOWER(CAST(b.synopsis AS string)) LIKE CONCAT('%', LOWER(:searchTerm), '%') " + // Adicionado CAST
            "OR LOWER(b.keywords) LIKE CONCAT('%', LOWER(:searchTerm), '%'))")
    List<Book> searchByTermInMultipleFields(@Param("status") Book.Status status, @Param("searchTerm") String searchTerm);
}
