package com.books.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleCreateRequest {

    @NotNull(message = "A data do agendamento é obrigatória.")
    @FutureOrPresent(message = "A data do agendamento não pode ser no passado.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Formato esperado do JSON
    private LocalDateTime scheduleDate;

    @NotNull(message = "A duração em dias é obrigatória.")
    @Min(value = 1, message = "A duração deve ser de pelo menos 1 dia.")
    private Integer durationDays;

    @NotNull(message = "A lista de IDs de livros é obrigatória.")
    @Size(min = 1, message = "Pelo menos um livro deve ser selecionado para o agendamento.")
    private List<Long> bookIds;
}
