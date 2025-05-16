// Define o pacote da classe (estrutura de pastas no projeto)
package com.books.api.model;

// Importações necessárias
import jakarta.persistence.*; // Anotações para mapear a classe como entidade do banco (JPA)
import lombok.*;             // Anotações para gerar automaticamente getters, setters, etc.
import java.time.LocalDateTime; // Classe para armazenar data e hora

// Indica que essa classe representa uma entidade JPA, ou seja, uma tabela no banco de dados
@Entity
// Lombok: cria automaticamente os getters e setters para todos os atributos
@Getter @Setter
// Lombok: cria um construtor vazio (sem argumentos)
@NoArgsConstructor
// Lombok: cria um construtor com todos os argumentos
@AllArgsConstructor
// Lombok: permite usar o padrão de projeto Builder para criar objetos de forma fluente
@Builder
public class Booking {

    // Indica que esse campo é a chave primária da tabela
    @Id
    // Define que o valor do ID será gerado automaticamente pelo banco de dados
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do cliente que fez o agendamento
    private String customerName;

    // Título do livro que está sendo agendado
    private String bookTitle;

    // Data e hora em que o agendamento foi feito
    private LocalDateTime bookingDate;

    // Data e hora em que o livro foi devolvido
    private LocalDateTime returnDate;

    // Status atual do agendamento (ex: AGENDADO, DEVOLVIDO, CANCELADO...)
    @Enumerated(EnumType.STRING) // Salva o valor do enum como texto (e não como número)
    private BookingStatus status;
}
