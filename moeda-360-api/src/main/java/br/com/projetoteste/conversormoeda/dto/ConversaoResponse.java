package br.com.projetoteste.conversormoeda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ConversaoResponse(
        BigDecimal valorOriginal,
        Moeda moedaOrigem,
        BigDecimal cotacaoConversao,
        BigDecimal valorConvertido,
        Moeda moedaDestino,
        LocalDate dataCotacao,
        OffsetDateTime consultadoEm
) {
}