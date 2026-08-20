# ADR 001: monorepo e limites hexagonais

Status: aceita em 20 de agosto de 2026.

## Contexto

O projeto precisa demonstrar um fluxo completo entre Angular e Spring Boot, mas as regras de transição não podem depender do framework ou da interface.

## Decisão

Backend, frontend, especificações e automações ficam no mesmo repositório. No backend, o domínio e os casos de uso dependem de portas; HTTP, JPA, MCP e planejamento são adaptadores.

## Consequências

Um único pull request consegue alterar contrato e interface de forma rastreável. O repositório fica maior e exige pipelines separados. A disciplina de limites será verificada por testes de arquitetura.
