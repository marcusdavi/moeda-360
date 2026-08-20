# Conversor de moedas

API Spring Boot que converte um valor em reais para dolares usando a cotacao `USD-BRL` consultada em tempo real na AwesomeAPI.

## Executar

Requer Java 21+ e Maven 3.9+.

```bash
mvn spring-boot:run
```

## Endpoint

```bash
curl -X POST http://localhost:8080/api/conversoes \
  -H 'Content-Type: application/json' \
  -d '{"valorEmReais": 100, "data": "2026-08-19"}'
```

Resposta:

```json
{
  "valorEmReais": 100.00,
  "cotacaoDolar": 5.1234,
  "valorEmDolares": 19.52,
  "dataCotacao": "2026-08-19",
  "consultadoEm": "2026-08-20T12:00:00-03:00"
}
```

O campo opcional `data` deve estar no formato `YYYY-MM-DD` e ser anterior ao dia atual. Quando informado, a API busca o fechamento do dolar naquela data; sem ele, usa a cotacao atual. O campo `consultadoEm` informa o horario em que a conversao foi realizada. Em caso de indisponibilidade da cotacao, a API retorna HTTP 502.