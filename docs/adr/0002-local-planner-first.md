# ADR 002: planejador local antes da integração com LLM

Status: aceita em 20 de agosto de 2026.

## Contexto

Um recurso agêntico que depende de chave externa deixaria a demonstração frágil e dificultaria testes reproduzíveis.

## Decisão

O primeiro adaptador gera um plano determinístico a partir dos critérios de aceite. Uma porta permite adicionar Claude, Codex ou outro modelo depois, sem alterar o caso de uso.

## Consequências

O projeto demonstra decomposição, rastreabilidade e guardrails sem fingir capacidade generativa. O plano local é menos flexível, mas funciona offline e pode ser testado por igualdade.
