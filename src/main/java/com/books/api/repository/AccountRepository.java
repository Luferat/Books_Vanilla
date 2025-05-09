package com.books.api.repository;

import com.books.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositórios (Repositories) são interfaces responsáveis por acessar e manipular os dados no banco.
 * Com o Spring Data JPA, podemos estender interfaces como JpaRepository para ganhar métodos prontos de CRUD,
 * sem precisar implementar consultas básicas manualmente.
 * <p>
 * Este repositório lida com a entidade Account, permitindo operações como salvar, buscar, atualizar e deletar contas.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Busca uma conta pelo e-mail fornecido.
     *
     * @param email O e-mail da conta a ser localizada.
     * @return Um Optional contendo a conta, se encontrada.
     */
    Optional<Account> findByEmail(String email);
}
