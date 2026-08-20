package br.com.projetoteste.conversormoeda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record ConversaoRequest(
        @NotNull(message = "valorEmReais e obrigatorio")
        @DecimalMin(value = "0.01", message = "valorEmReais deve ser maior que zero")
                BigDecimal valor,
                @NotNull(message = "moedaOrigem e obrigatoria")
                Moeda moedaOrigem,
                @NotNull(message = "moedaDestino e obrigatoria")
                Moeda moedaDestino,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Past(message = "data deve ser anterior ao dia atual")
        LocalDate data
) {

        @AssertTrue(message = "moedaOrigem e moedaDestino devem ser diferentes")
        public boolean isMoedasDiferentes() {
                return moedaOrigem == null || moedaDestino == null || moedaOrigem != moedaDestino;
        }
}