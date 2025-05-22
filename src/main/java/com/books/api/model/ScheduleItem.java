package com.books.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "schedule_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule; // O agendamento ao qual este item pertence

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // O livro alugado neste item

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1; // Quantidade do livro neste agendamento (sempre 1 por enquanto)
}
