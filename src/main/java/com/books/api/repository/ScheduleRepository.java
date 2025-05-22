package com.books.api.repository;

import com.books.api.model.Account;
import com.books.api.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    /**
     * Busca todos os agendamentos associados a uma conta específica,
     * carregando eagermente os detalhes da conta, os itens do agendamento
     * e os livros associados a esses itens.
     *
     * @param account A conta para a qual buscar os agendamentos.
     * @return Uma lista de agendamentos para a conta especificada.
     */
    @Query("SELECT s FROM Schedule s JOIN FETCH s.account a LEFT JOIN FETCH s.items si LEFT JOIN FETCH si.book b WHERE a = :account")
    List<Schedule> findByAccountWithDetails(Account account);
}
