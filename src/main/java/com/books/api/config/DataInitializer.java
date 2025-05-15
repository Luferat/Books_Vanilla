package com.books.api.config;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AccountRepository accountRepository;

    @PostConstruct
    public void init() {
        if (accountRepository.count() > 0) return;

        String encryptedPassword = new BCryptPasswordEncoder().encode("Senha123");

        // Criando todas as contas
        Account account1 = new Account(null, LocalDateTime.now().minusDays(113), "https://randomuser.me/api/portraits/men/11.jpg", "(21) 99887-7665", LocalDate.of(2000, 11, 12), "Joca da Silva", "joca@email.com", "999.888.777-66", encryptedPassword, Account.Role.ADMIN, Account.Status.ON, null, "Rua das Flores, 123");
        Account account2 = new Account(null, LocalDateTime.now().minusDays(107), "https://randomuser.me/api/portraits/women/11.jpg", "(21) 98765-4321", LocalDate.of(1984, 8, 30), "Marineuza Siriliano", "marineuza@email.com", "888.777.666-55", encryptedPassword, Account.Role.ADMIN, Account.Status.ON, null, "Av. Brasil, 456");
        Account account3 = new Account(null, LocalDateTime.now().minusDays(92), "https://randomuser.me/api/portraits/men/12.jpg", "(21) 98989-7676", LocalDate.of(1992, 1, 24), "Dilermano Souza", "dilermano@email.com", "777.666.555-44", encryptedPassword, Account.Role.OPERATOR, Account.Status.ON, null, "Rua Central, 789");
        Account account4 = new Account(null, LocalDateTime.now().minusDays(81), "https://randomuser.me/api/portraits/men/13.jpg", "(21) 98877-6665", LocalDate.of(2001, 3, 29), "Setembrino Trocatapas", "setembrino@email.com", "666.555.444-33", encryptedPassword, Account.Role.OPERATOR, Account.Status.ON, null, "Av. Paulista, 321");
        Account account5 = new Account(null, LocalDateTime.now().minusDays(74), "https://randomuser.me/api/portraits/women/12.jpg", "(21) 98798-7987", LocalDate.of(1981, 9, 20), "Hemengarda Sirigarda", "hemengarda@email.com", "555.444.333-22", encryptedPassword, Account.Role.OPERATOR, Account.Status.ON, null, "Rua das Acácias, 654");
        Account account6 = new Account(null, LocalDateTime.now().minusDays(63), "https://randomuser.me/api/portraits/men/14.jpg", "(21) 99988-8777", LocalDate.of(1981, 9, 20), "Fernandino Nomecladastio", "fernandino@email.com", "444.333.222-11", encryptedPassword, Account.Role.USER, Account.Status.OFF, null, "Rua Bela Vista, 987");
        Account account7 = new Account(null, LocalDateTime.now().minusDays(57), "https://randomuser.me/api/portraits/women/13.jpg", "(21) 98798-7997", LocalDate.of(1981, 9, 20), "Salestiana Correntina", "salestiana@email.com", "333.222.111-00", encryptedPassword, Account.Role.USER, Account.Status.ON, null, "Rua do Sol, 147");
        Account account8 = new Account(null, LocalDateTime.now().minusDays(45), "https://randomuser.me/api/portraits/women/14.jpg", "(21) 91111-2222", LocalDate.of(1990, 7, 12), "Zuleica Navalha", "zuleica@email.com", "222.111.000-99", encryptedPassword, Account.Role.USER, Account.Status.ON, null, "Av. Liberdade, 258");
        Account account9 = new Account(null, LocalDateTime.now().minusDays(31), "https://randomuser.me/api/portraits/men/15.jpg", "(21) 92222-3333", LocalDate.of(1987, 5, 3), "Brunildo Cortês", "brunildo@email.com", "333.222.111-88", encryptedPassword, Account.Role.USER, Account.Status.ON, null, "Rua Harmonia, 369");
        Account account10 = new Account(null, LocalDateTime.now().minusDays(22), "https://randomuser.me/api/portraits/women/15.jpg", "(21) 93333-4444", LocalDate.of(1995, 12, 9), "Clarice Estilosa", "clarice@email.com", "444.333.222-77", encryptedPassword, Account.Role.USER, Account.Status.ON, null, "Av. da Paz, 159");
        Account account11 = new Account(null, LocalDateTime.now().minusDays(11), "https://randomuser.me/api/portraits/men/16.jpg", "(21) 94444-5555", LocalDate.of(1993, 10, 18), "Genivaldo Bicheiro", "genivaldo@email.com", "555.444.333-66", encryptedPassword, Account.Role.USER, Account.Status.ON, null, "Rua Certa, 999");

        // Salvando todas as contas
        List<Account> accounts = List.of(account1, account2, account3, account4, account5, account6, account7, account8, account9, account10, account11);
        accountRepository.saveAll(accounts);
    }
}