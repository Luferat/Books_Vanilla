package com.books.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String isbn;
    private String publisher;

    @Column(nullable = false)
    private Integer publicationYear;

    private Integer editionNumber;
    private Integer numberOfPages;
    private String genre;
    private String format;
    private String accessUrl; // Para e-books
    private String fileFormat; // Para e-books
    private Boolean hasDrm; // Para e-books
    private String language;

    @Lob
    private String synopsis;

    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Book.Status status;
    private boolean ebook;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private Integer stock; // Campo adicionado

    public enum Status {
        ON,
        OFF
    }
}
