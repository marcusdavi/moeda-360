# moeda-360

Conversor de moedas que consulta cotações em relação ao real e converte valores entre reais, dólares, euros e libras esterlinas.

## Sobre este projeto

Este é um projetinho criado **100% com inteligência artificial**.

Todo o código deste repositório foi gerado por IA, sem um humano escrevendo ou alterando manualmente o código-fonte. A implementação, a estrutura dos projetos, a documentação e os ajustes foram realizados por meio de instruções em linguagem natural para a IA.

## Estrutura

- `moeda-360-api/`: backend em Java 21 com Spring Boot
- `moeda-360-web/`: frontend em React com Vite

## Como executar

### Backend

Requisitos: Java 21+ e Maven 3.9+.

```bash
cd moeda-360-api
mvn spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

### Frontend

Requisitos: Node.js 20+.

```bash
cd moeda-360-web
npm install
npm run dev
```

O frontend ficará disponível em `http://localhost:5173`.

Durante o desenvolvimento, o Vite encaminha as requisições `/api` para o backend em `http://localhost:8080`.

## Funcionalidades

- Conversão entre BRL, USD, EUR e GBP em qualquer direção
- Seleção da moeda de origem e da moeda de destino na interface
- Consulta da cotação atual
- Consulta de cotação histórica por data
- Formatação de valores no padrão brasileiro
- Histórico das cinco últimas conversões da sessão
- Tratamento de erros de validação e indisponibilidade da cotação

## Endpoint principal

```http
POST /api/conversoes
Content-Type: application/json
```

Exemplo de requisição:

```json
{
  "valor": 100,
  "moedaOrigem": "BRL",
  "moedaDestino": "USD",
  "data": "2026-08-19"
}
```

O campo `data` é opcional e deve ser anterior ao dia atual.

## Tecnologias

- Java 21
- Spring Boot
- Maven
- React
- Vite
- Lucide React
- AwesomeAPI

## Licença

Projeto experimental criado para fins de estudo e demonstração.
