package br.com.projetoteste.conversormoeda.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConversaoRequest(
        @NotNull(message = "valorEmReais e obrigatorio")
        @DecimalMin(value = "0.01", message = "valorEmReais deve ser maior que zero")
        BigDecimal valorEmReais,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Past(message = "data deve ser anterior ao dia atual")
        LocalDate data
) {
}