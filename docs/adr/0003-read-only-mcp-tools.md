# ADR 003: ferramentas MCP somente leitura

Status: aceita em 20 de agosto de 2026.

## Contexto

Ferramentas chamadas por agentes podem produzir efeitos difíceis de perceber. Alterar status ou liberar uma release exige julgamento humano.

## Decisão

O servidor MCP inicial expõe apenas listagem, consulta e avaliação de prontidão. Operações de escrita permanecem na API autenticada e na interface.

## Consequências

O agente recebe contexto suficiente para planejar e revisar. Ele não consegue aprovar ou liberar uma mudança. Se ferramentas de escrita forem adicionadas, cada chamada deverá exigir confirmação explícita e auditoria.
