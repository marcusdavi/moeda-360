package br.com.projetoteste.conversormoeda.integration;

public class CotacaoIndisponivelException extends RuntimeException {

    public CotacaoIndisponivelException() {
        super("Nao foi possivel obter a cotacao atual do dolar");
    }
}