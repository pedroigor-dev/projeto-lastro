# ADR 002: planejador local determinístico

Status: aceita em 20 de agosto de 2026.

## Contexto

Uma dependência remota obrigatória deixaria a demonstração frágil e dificultaria testes reproduzíveis.

## Decisão

O primeiro adaptador gera um plano determinístico a partir dos critérios de aceite. Uma porta permite adicionar outros adaptadores depois, sem alterar o caso de uso.

## Consequências

O projeto demonstra decomposição, rastreabilidade e controles verificáveis. O plano local é menos flexível, mas funciona offline e pode ser testado por igualdade.
