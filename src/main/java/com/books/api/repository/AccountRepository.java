package com.books.api.repository;

import com.books.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByCpf(String cpf);

    Optional<Account> findByEmailAndCpfAndBirth(String email, String cpf, LocalDate birth);

}
