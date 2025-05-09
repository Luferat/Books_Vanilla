package com.books.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Modelos (Models) são classes que representam as entidades do sistema.
 * No contexto de aplicações Java com Spring e JPA, os Models correspondem a tabelas no banco de dados,
 * definindo os campos, tipos de dados e regras de persistência.
 *
 * Esta classe representa a entidade "Account" (Conta de Usuário), usada para armazenar dados de usuários no sistema.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    /**
     * Identificador único da conta (chave primária).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data e hora de criação da conta.
     * É preenchida automaticamente pelo banco de dados.
     */
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    /**
     * Nome do usuário.
     */
    @Column(nullable = false)
    private String name;

    /**
     * E-mail do usuário.
     * E-mail não pode existir no bd.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Senha do usuário (armazenada de forma criptografada).
     * Não pode ter menos que 8 caracteres.
     */
    @Column(nullable = false)
    private String password;

    /**
     * URL ou caminho da foto de perfil.
     */
    @Column(nullable = false)
    private String photo;

    /**
     * CPF do usuário (documento brasileiro).
     * CPF não pode existir no bd
     */
    @Column(nullable = false, unique = true)
    private String cpf;

    /**
     * Telefone de contato do usuário.
     *
     */
    @Column(unique = true)
    private String tel;

    /**
     * Data de nascimento do usuário.
     * Tem que ser uma data válida.
     */
    @Column(nullable = false)
    private LocalDate birth;

    /**
     * Endereço completo (armazenado como texto grande).
     */
    @Lob
    private String address;

    /**
     * Nível de acesso do usuário (padrão: USER).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'USER'")
    private Role role = Role.USER;

    /**
     * Status da conta (padrão: ON).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(3) DEFAULT 'ON'")
    private Status status = Status.ON;

    /**
     * Dados adicionais em formato texto (JSON ou outros).
     */
    @Lob
    private String metadata;

    /**
     * Enumeração dos papéis possíveis para uma conta.
     * USER: Usuário comum
     * ADMIN: Administrador do sistema
     * OPERATOR: Usuário com permissões intermediárias
     */
    public enum Role {
        USER,
        ADMIN,
        OPERATOR
    }

    /**
     * Enumeração do status da conta.
     * ON: Ativa
     * OFF: Inativa
     */
    public enum Status {
        ON,
        OFF
    }
}


