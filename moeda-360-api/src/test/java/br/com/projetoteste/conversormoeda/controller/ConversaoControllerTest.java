package br.com.projetoteste.conversormoeda.controller;

import br.com.projetoteste.conversormoeda.dto.ConversaoResponse;
import br.com.projetoteste.conversormoeda.dto.Moeda;
import br.com.projetoteste.conversormoeda.service.ConversaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversaoController.class)
class ConversaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversaoService conversaoService;

    @Test
    void deveConverterValorEmReais() throws Exception {
        when(conversaoService.converter(any(), any(), any(), any())).thenReturn(new ConversaoResponse(
                new BigDecimal("100.00"), Moeda.BRL, new BigDecimal("5.00"),
            new BigDecimal("20.00"), Moeda.USD, null, OffsetDateTime.now()));

        mockMvc.perform(post("/api/conversoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100,\"moedaOrigem\":\"BRL\",\"moedaDestino\":\"USD\"}"))
                .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valorConvertido").value(20.0));
    }

    @Test
    void deveRejeitarValorInvalido() throws Exception {
        mockMvc.perform(post("/api/conversoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":0,\"moedaOrigem\":\"BRL\",\"moedaDestino\":\"USD\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarMoedasDeOrigemEDestinoIguais() throws Exception {
        mockMvc.perform(post("/api/conversoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100,\"moedaOrigem\":\"EUR\",\"moedaDestino\":\"EUR\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAceitarDataAnteriorAoDiaAtual() throws Exception {
        LocalDate data = LocalDate.now().minusDays(1);
        when(conversaoService.converter(any(), any(), any(), any())).thenReturn(new ConversaoResponse(
                new BigDecimal("100.00"), Moeda.BRL, new BigDecimal("5.00"),
            new BigDecimal("20.00"), Moeda.USD, data, OffsetDateTime.now()));

        mockMvc.perform(post("/api/conversoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100,\"moedaOrigem\":\"BRL\",\"moedaDestino\":\"USD\",\"data\":\"" + data + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataCotacao").value(data.toString()));
    }

    @Test
    void deveRejeitarDataAtual() throws Exception {
        mockMvc.perform(post("/api/conversoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100,\"moedaOrigem\":\"BRL\",\"moedaDestino\":\"USD\",\"data\":\"" + LocalDate.now() + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarDataForaDoFormatoIso() throws Exception {
        mockMvc.perform(post("/api/conversoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100,\"moedaOrigem\":\"BRL\",\"moedaDestino\":\"USD\",\"data\":\"2026-8-19\"}"))
                .andExpect(status().isBadRequest());
    }

                @Test
                void deveConverterDolaresParaReais() throws Exception {
                when(conversaoService.converter(any(), any(), any(), any())).thenReturn(new ConversaoResponse(
                    new BigDecimal("20.00"), Moeda.USD, new BigDecimal("5.00"),
                    new BigDecimal("100.00"), Moeda.BRL, null, OffsetDateTime.now()));

                mockMvc.perform(post("/api/conversoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":20,\"moedaOrigem\":\"USD\",\"moedaDestino\":\"BRL\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valorConvertido").value(100.0))
                    .andExpect(jsonPath("$.moedaOrigem").value("USD"))
                    .andExpect(jsonPath("$.moedaDestino").value("BRL"));
                }
}