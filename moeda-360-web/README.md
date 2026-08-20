# moeda-360-web

Frontend React para conversao bidirecional entre BRL e USD, integrado a API `moeda-360-api`.

## Executar

Requer Node.js 20+.

```bash
npm install
npm run dev
```

O Vite encaminha `/api` para `http://localhost:8080` durante o desenvolvimento. Para apontar para outra URL, copie `.env.example` para `.env` e defina `VITE_API_URL`.

Na interface, use o botao de inversao para alternar entre BRL para USD e USD para BRL.
