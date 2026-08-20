package br.com.projetoteste.conversormoeda.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import br.com.projetoteste.conversormoeda.dto.ConversaoResponse;
import br.com.projetoteste.conversormoeda.dto.Moeda;
import br.com.projetoteste.conversormoeda.integration.AwesomeApiClient;

@Service
public class ConversaoService {

    private final AwesomeApiClient awesomeApiClient;
    private final ConcurrentMap<ConversaoCacheKey, ConversaoResponse> conversoesHistoricas = new ConcurrentHashMap<>();

    public ConversaoService(AwesomeApiClient awesomeApiClient) {
        this.awesomeApiClient = awesomeApiClient;
    }

    public ConversaoResponse converter(BigDecimal valor, Moeda moedaOrigem, Moeda moedaDestino, LocalDate data) {
        if (data != null) {
            ConversaoCacheKey cacheKey = new ConversaoCacheKey(valor, moedaOrigem, moedaDestino, data);
            return conversoesHistoricas.computeIfAbsent(cacheKey,
                    key -> criarConversao(key.valor(), key.moedaOrigem(), key.moedaDestino(), data));
        }
        return criarConversao(valor, moedaOrigem, moedaDestino, null);
    }

    private ConversaoResponse criarConversao(BigDecimal valor, Moeda moedaOrigem, Moeda moedaDestino, LocalDate data) {
        BigDecimal cotacaoOrigem = data == null
            ? awesomeApiClient.buscarCotacao(moedaOrigem)
            : awesomeApiClient.buscarCotacao(moedaOrigem, data);
        BigDecimal cotacaoDestino = data == null
            ? awesomeApiClient.buscarCotacao(moedaDestino)
            : awesomeApiClient.buscarCotacao(moedaDestino, data);
        BigDecimal valorConvertido = valor.multiply(cotacaoOrigem)
            .divide(cotacaoDestino, 2, RoundingMode.HALF_UP);
        BigDecimal cotacaoConversao = cotacaoOrigem.divide(cotacaoDestino, 6, RoundingMode.HALF_UP);

        return new ConversaoResponse(
                valor.setScale(2, RoundingMode.HALF_UP),
                moedaOrigem,
                cotacaoConversao,
                valorConvertido,
                moedaDestino,
                data,
                OffsetDateTime.now()
        );
    }

    private record ConversaoCacheKey(BigDecimal valor, Moeda moedaOrigem, Moeda moedaDestino, LocalDate data) {

        private ConversaoCacheKey {
            valor = valor.stripTrailingZeros();
        }
    }
}