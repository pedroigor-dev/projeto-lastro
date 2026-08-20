# Regras para agentes de desenvolvimento

Este repositório usa Spec-Driven Development. Antes de alterar comportamento:

1. leia `specs/001-projeto-lastro/spec.md` e a ADR relacionada;
2. identifique o critério de aceite atendido pela mudança;
3. escreva ou atualize o teste que demonstra o comportamento;
4. mantenha domínio e casos de uso independentes de HTTP, JPA e Angular;
5. registre decisões que alterem limites arquiteturais em `docs/adr/`;
6. não marque um gate como aprovado sem evidência verificável;
7. nunca grave chaves, tokens ou dados pessoais no repositório.

## Comandos de validação

- Backend: `cd backend && ./mvnw verify`
- Frontend: `cd frontend && npm ci && npm run test:ci && npm run build`
- Ambiente local: `docker compose up --build`

## Definição de pronto

Uma mudança está pronta quando os critérios afetados possuem testes, as verificações locais passam e a documentação descreve qualquer novo trade-off relevante.
