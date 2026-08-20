package br.com.projetoteste.conversormoeda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ConversaoResponse(
        BigDecimal valorEmReais,
        BigDecimal cotacaoDolar,
        BigDecimal valorEmDolares,
        LocalDate dataCotacao,
        OffsetDateTime consultadoEm
) {
}