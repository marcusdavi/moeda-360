package br.com.projetoteste.conversormoeda.controller;

import br.com.projetoteste.conversormoeda.dto.ConversaoRequest;
import br.com.projetoteste.conversormoeda.dto.ConversaoResponse;
import br.com.projetoteste.conversormoeda.service.ConversaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversoes")
public class ConversaoController {

    private final ConversaoService conversaoService;

    public ConversaoController(ConversaoService conversaoService) {
        this.conversaoService = conversaoService;
    }

    @PostMapping
    public ResponseEntity<ConversaoResponse> converter(@Valid @RequestBody ConversaoRequest request) {
        return ResponseEntity.ok(conversaoService.converter(
                request.valor(), request.moedaOrigem(), request.moedaDestino(), request.data()));
    }
}