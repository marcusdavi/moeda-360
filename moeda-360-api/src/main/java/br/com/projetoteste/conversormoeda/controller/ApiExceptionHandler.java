package br.com.projetoteste.conversormoeda.controller;

import br.com.projetoteste.conversormoeda.integration.CotacaoIndisponivelException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CotacaoIndisponivelException.class)
    public ProblemDetail tratarCotacaoIndisponivel(CotacaoIndisponivelException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, exception.getMessage());
        problem.setTitle("Cotacao indisponivel");
        return problem;
    }
}