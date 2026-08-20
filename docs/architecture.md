# Arquitetura e fluxos

## Limites

O backend adota portas e adaptadores. O domínio conhece especificações, critérios, gates e transições. Ele não conhece HTTP, JPA, PostgreSQL, MCP ou o planejador usado na borda.

O Angular consome a API por contratos tipados e mantém estado de tela com signals. Regras que protegem integridade continuam no backend; a interface apenas antecipa validações para melhorar a experiência.

## Visão de componentes

```mermaid
flowchart TB
    USER["Produto, engenharia e revisão"] --> WEB["Aplicação Angular"]
    WEB --> HTTP["API REST Spring Boot"]
    CLIENT["Cliente MCP"] --> MCP["Adaptador MCP"]
    MCP --> USE["Casos de uso"]
    HTTP --> USE
    USE --> DOMAIN["Domínio"]
    USE --> REPO["Porta de persistência"]
    USE --> PLANNER["Porta do planejador"]
    JPA["Adaptador JPA"] --> REPO
    LOCAL["Planejador determinístico"] --> PLANNER
    JPA --> DB[("PostgreSQL")]
```

## Fluxo de release

```mermaid
sequenceDiagram
    participant U as Pessoa revisora
    participant A as API
    participant D as Domínio
    participant P as PostgreSQL

    U->>A: solicitar avaliação de prontidão
    A->>P: carregar especificação e evidências
    A->>D: evaluateReadiness()
    D-->>A: pronta ou lista de pendências
    alt existem pendências
        A-->>U: 200 com bloqueios explícitos
    else pronta para release
        U->>A: confirmar release
        A->>D: release()
        A->>P: salvar estado e evento de auditoria
        A-->>U: especificação RELEASED
    end
```

## Decisões relacionadas

- [ADR 001: monorepo e limites hexagonais](adr/0001-monorepo-and-hexagonal-boundaries.md)
- [ADR 002: agente local antes de integração com LLM](adr/0002-local-planner-first.md)
- [ADR 003: ferramentas MCP somente leitura](adr/0003-read-only-mcp-tools.md)
