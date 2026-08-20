package br.com.projetoteste.conversormoeda.service;

import br.com.projetoteste.conversormoeda.dto.ConversaoResponse;
import br.com.projetoteste.conversormoeda.integration.AwesomeApiClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ConversaoService {

    private final AwesomeApiClient awesomeApiClient;
    private final ConcurrentMap<ConversaoCacheKey, ConversaoResponse> conversoesHistoricas = new ConcurrentHashMap<>();

    public ConversaoService(AwesomeApiClient awesomeApiClient) {
        this.awesomeApiClient = awesomeApiClient;
    }

    public ConversaoResponse converter(BigDecimal valorEmReais, LocalDate data) {
        if (data != null) {
            ConversaoCacheKey cacheKey = new ConversaoCacheKey(valorEmReais, data);
            return conversoesHistoricas.computeIfAbsent(cacheKey, key -> criarConversao(key.valorEmReais(), data));
        }
        return criarConversao(valorEmReais, null);
    }

    private ConversaoResponse criarConversao(BigDecimal valorEmReais, LocalDate data) {
        BigDecimal cotacaoDolar = data == null
                ? awesomeApiClient.buscarCotacaoDolar()
                : awesomeApiClient.buscarCotacaoDolar(data);
        BigDecimal valorEmDolares = valorEmReais.divide(cotacaoDolar, 2, RoundingMode.HALF_UP);

        return new ConversaoResponse(
                valorEmReais.setScale(2, RoundingMode.HALF_UP),
                cotacaoDolar.setScale(4, RoundingMode.HALF_UP),
                valorEmDolares,
                data,
                OffsetDateTime.now()
        );
    }

    private record ConversaoCacheKey(BigDecimal valorEmReais, LocalDate data) {

        private ConversaoCacheKey {
            valorEmReais = valorEmReais.stripTrailingZeros();
        }
    }
}