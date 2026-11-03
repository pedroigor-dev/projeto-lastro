# ADR 003: ferramentas MCP somente leitura

Status: aceita em 20 de agosto de 2026.

## Contexto

Interfaces de integração podem produzir efeitos difíceis de perceber. Alterar status ou liberar uma release exige revisão e autorização.

## Decisão

O servidor MCP inicial expõe apenas listagem, consulta e avaliação de prontidão. Operações de escrita permanecem na API autenticada e na interface.

## Consequências

O cliente recebe contexto para consultar e revisar. Ele não consegue aprovar ou liberar uma mudança. Se operações de escrita forem adicionadas, cada chamada deverá exigir autorização explícita e auditoria.
