package com.books.api.service;

import com.books.api.model.Account;
import com.books.api.model.Book;
import com.books.api.model.Schedule;
import com.books.api.model.Schedule.Status;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Schedule createSchedule(Long accountId, LocalDateTime scheduleDate, Integer durationDays, List<Long> bookIds) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado ou não logado");
        }
        Account account = accountOpt.get();

        List<Book> books = bookRepository.findAllById(bookIds);
        if (books.size() != bookIds.size()) {
            throw new IllegalArgumentException("Alguns livros não foram encontrados");
        }

        // Verifica estoque de cada book
        for (Book book : books) {
            if (book.getStock() == null || book.getStock() <= 0) {
                throw new IllegalStateException("Livro " + book.getTitle() + " sem estoque disponível");
            }
        }

        // Verifica se os livros já estão agendados em conflito (ex: para as mesmas datas)
        for (Book book : books) {
            boolean isAgendado = scheduleRepository.existsByBookAndStatusAndScheduleDateBetween(
                    book, Status.SCHEDULED,
                    scheduleDate,
                    scheduleDate.plusDays(durationDays)
            );
            if (isAgendado) {
                throw new IllegalStateException("Livro " + book.getTitle() + " já está agendado neste período");
            }
        }

        // Cria o agendamento
        Schedule schedule = Schedule.builder()
                .account(account)
                .scheduleDate(scheduleDate)
                .durationDays(durationDays)
                .status(Status.SCHEDULED)
                .books(books)
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        // Atualiza estoque dos livros (diminui 1)
        for (Book book : books) {
            book.setStock(book.getStock() - 1);
            bookRepository.save(book);
        }

        return savedSchedule;
    }
}
