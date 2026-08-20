package br.com.projetoteste.conversormoeda.service;

import br.com.projetoteste.conversormoeda.dto.ConversaoResponse;
import br.com.projetoteste.conversormoeda.integration.AwesomeApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversaoServiceTest {

    @Mock
    private AwesomeApiClient awesomeApiClient;

    @InjectMocks
    private ConversaoService conversaoService;

    @Test
    void naoDeveBuscarNovamenteParaMesmoValorEDataHistorica() {
        LocalDate data = LocalDate.of(2020, 1, 2);
        when(awesomeApiClient.buscarCotacaoDolar(data)).thenReturn(new BigDecimal("4.025"));

        ConversaoResponse primeiraResposta = conversaoService.converter(new BigDecimal("100.00"), data);
        ConversaoResponse segundaResposta = conversaoService.converter(new BigDecimal("100.0"), data);

        assertEquals(primeiraResposta, segundaResposta);
        verify(awesomeApiClient, times(1)).buscarCotacaoDolar(data);
    }
}