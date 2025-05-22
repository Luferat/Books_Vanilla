package com.books.api.repository;

import com.books.api.model.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    // Métodos de consulta personalizados para ScheduleItem, se necessário
}
