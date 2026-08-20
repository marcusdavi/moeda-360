package br.com.projetoteste.conversormoeda.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class AwesomeApiClient {

    private final RestClient restClient;

    public AwesomeApiClient(@Value("${cotacao.api.url}") String apiUrl) {
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
    }

    public BigDecimal buscarCotacaoDolar() {
        try {
            Map<String, CotacaoResponse> response = restClient.get()
                    .uri("/last/USD-BRL")
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});

            if (response == null || response.get("USDBRL") == null || response.get("USDBRL").bid() == null) {
                throw new CotacaoIndisponivelException();
            }
            return new BigDecimal(response.get("USDBRL").bid());
        } catch (RestClientException | NumberFormatException exception) {
            throw new CotacaoIndisponivelException();
        }
    }

    public BigDecimal buscarCotacaoDolar(LocalDate data) {
        try {
            List<CotacaoResponse> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/daily/USD-BRL/1")
                            .queryParam("start_date", data.format(DateTimeFormatter.BASIC_ISO_DATE))
                            .queryParam("end_date", data.format(DateTimeFormatter.BASIC_ISO_DATE))
                            .build())
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});

            if (response == null || response.isEmpty() || response.get(0).bid() == null) {
                throw new CotacaoIndisponivelException();
            }
            return new BigDecimal(response.get(0).bid());
        } catch (RestClientException | NumberFormatException exception) {
            throw new CotacaoIndisponivelException();
        }
    }

    private record CotacaoResponse(String bid) {
    }
}