# moeda-360-web

Frontend React para conversao entre BRL, USD, EUR e GBP, integrado a API `moeda-360-api`.

## Executar

Requer Node.js 20+.

```bash
npm install
npm run dev
```

O Vite encaminha `/api` para `http://localhost:8080` durante o desenvolvimento. Para apontar para outra URL, copie `.env.example` para `.env` e defina `VITE_API_URL`.

Na interface, use o botao de inversao para trocar rapidamente as moedas selecionadas.

Os seletores permitem escolher qualquer uma das quatro moedas como origem e destino.
