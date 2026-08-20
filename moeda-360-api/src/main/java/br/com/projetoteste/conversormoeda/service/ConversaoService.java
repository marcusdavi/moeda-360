package br.com.projetoteste.conversormoeda.service;

import br.com.projetoteste.conversormoeda.dto.ConversaoResponse;
import br.com.projetoteste.conversormoeda.dto.Moeda;
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

    public ConversaoResponse converter(BigDecimal valor, Moeda moedaOrigem, Moeda moedaDestino, LocalDate data) {
        if (data != null) {
            ConversaoCacheKey cacheKey = new ConversaoCacheKey(valor, moedaOrigem, moedaDestino, data);
            return conversoesHistoricas.computeIfAbsent(cacheKey,
                    key -> criarConversao(key.valor(), key.moedaOrigem(), key.moedaDestino(), data));
        }
        return criarConversao(valor, moedaOrigem, moedaDestino, null);
    }

    private ConversaoResponse criarConversao(BigDecimal valor, Moeda moedaOrigem, Moeda moedaDestino, LocalDate data) {
        BigDecimal cotacaoDolar = data == null
                ? awesomeApiClient.buscarCotacaoDolar()
                : awesomeApiClient.buscarCotacaoDolar(data);
        BigDecimal valorConvertido = moedaOrigem == Moeda.BRL
                ? valor.divide(cotacaoDolar, 2, RoundingMode.HALF_UP)
                : valor.multiply(cotacaoDolar).setScale(2, RoundingMode.HALF_UP);

        return new ConversaoResponse(
                valor.setScale(2, RoundingMode.HALF_UP),
                moedaOrigem,
                cotacaoDolar.setScale(4, RoundingMode.HALF_UP),
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