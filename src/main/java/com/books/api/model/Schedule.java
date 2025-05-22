package com.books.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime scheduleDate;

    @Column(nullable = false)
    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default // Define um valor padrão para o Builder
    private Status status = Status.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY) // Um agendamento pertence a uma conta
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleItem> items; // Itens do agendamento (quais livros foram alugados)

    public enum Status {
        SCHEDULED, // Agendado
        UPDATED,   // Agendamento atualizado
        RETURNED,  // Livros devolvidos
        CANCELED   // Agendamento cancelado
    }

    // Método para ser chamado antes da persistência para garantir createdAt
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
