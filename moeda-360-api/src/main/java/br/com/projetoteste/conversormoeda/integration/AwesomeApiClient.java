package br.com.projetoteste.conversormoeda.integration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import br.com.projetoteste.conversormoeda.dto.Moeda;

@Component
public class AwesomeApiClient {

    private final RestClient restClient;

    public AwesomeApiClient(@Value("${cotacao.api.url}") String apiUrl) {
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
    }

    public BigDecimal buscarCotacao(Moeda moeda) {
        if (moeda == Moeda.BRL) {
            return BigDecimal.ONE;
        }
        try {
            String par = moeda + "-BRL";
            Map<String, CotacaoResponse> response = restClient.get()
                    .uri("/last/" + par)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});

            String chave = moeda + "BRL";
            if (response == null || response.get(chave) == null || response.get(chave).bid() == null) {
                throw new CotacaoIndisponivelException();
            }
            return new BigDecimal(response.get(chave).bid());
        } catch (RestClientException | NumberFormatException exception) {
            throw new CotacaoIndisponivelException();
        }
    }

    public BigDecimal buscarCotacao(Moeda moeda, LocalDate data) {
        if (moeda == Moeda.BRL) {
            return BigDecimal.ONE;
        }
        try {
            String par = moeda + "-BRL";
            List<CotacaoResponse> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/daily/" + par + "/1")
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