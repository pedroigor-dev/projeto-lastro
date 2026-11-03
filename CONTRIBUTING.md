# Contribuição

Antes de alterar um comportamento, leia a especificação em `specs/` e os ADRs relacionados. Identifique o critério de aceite afetado e mantenha domínio e casos de uso independentes de HTTP, JPA e Angular.

Registre mudanças de arquitetura em `docs/adr/`. Uma evidência de qualidade precisa ser verificável; não marque um gate como aprovado sem referência reproduzível. Credenciais e dados pessoais não devem entrar no repositório.

## Validação

- Backend: `cd backend && ./mvnw verify`
- Frontend: `cd frontend && npm ci && npm run test:ci && npm run build`
- Ambiente local: `docker compose up --build`

A alteração está pronta quando os critérios afetados possuem testes, as verificações passam e a documentação explica os limites e decisões relevantes.
