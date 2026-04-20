# JAVAEE

## Acuses de lectura WebSocket (estilo WhatsApp)

Estados soportados:
- ENVIADO: mensaje guardado en servidor.
- ENTREGADO: el receptor lo recibe (incluye al reconectarse).
- LEIDO: solo cuando el cliente envia un acuse de lectura.

Puedes usar las 2 formas de acuse de lectura:

1. Marcar chat completo como leido (al abrir conversacion):

```json
{
	"accion": "LEIDO_CHAT",
	"chatId": 10
}
```

2. Marcar un mensaje puntual como leido:

```json
{
	"accion": "LEIDO_MENSAJE",
	"chatId": 10,
	"mensajeId": 245
}
```

Compatibilidad:
- Tambien se acepta `accion: "LEIDO"`.
- Con `LEIDO`, si envias `chatId` se marca el chat completo.
- Con `LEIDO`, si envias solo `mensajeId` se marca solo ese mensaje.
