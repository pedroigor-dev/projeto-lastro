# Especificação 001: fluxo de mudanças orientado por especificações

Status: aprovada para implementação

Autor: Pedro Igor Campos Costa

Local e ano: Salvador, 2026

## Problema

Uma mudança pode ter código, testes e pipeline verde e ainda assim chegar à release sem responder ao problema original. Isso acontece quando a especificação fica separada das evidências de implementação.

O Lastro deve manter o vínculo entre a necessidade, os critérios de aceite, a revisão técnica e os gates usados na decisão de release.

## Pessoas usuárias

- pessoa de produto que descreve o problema e os critérios de aceite;
- pessoa desenvolvedora que planeja e implementa a mudança;
- pessoa revisora que avalia riscos e evidências antes da aprovação;
- agente de desenvolvimento que consulta a especificação por MCP e propõe um plano verificável.

## Escopo do primeiro incremento

O sistema deve permitir:

1. criar e consultar especificações de mudança;
2. registrar critérios de aceite mensuráveis;
3. conduzir o estado `DRAFT -> IN_REVIEW -> APPROVED -> IMPLEMENTING -> RELEASED`;
4. devolver uma especificação em revisão para `DRAFT` com justificativa;
5. registrar evidências para os gates de backend, frontend, qualidade, segurança e notas de release;
6. calcular prontidão de release sem permitir que uma evidência ausente seja tratada como aprovação;
7. gerar um plano de implementação local e determinístico a partir da especificação;
8. expor consultas seguras por ferramentas MCP;
9. manter um histórico imutável das transições de estado.

## Fora do escopo

- substituir Jira, GitLab ou Jenkins;
- executar pipelines externos diretamente;
- usar um modelo de linguagem obrigatório para o fluxo principal;
- gerenciar segredos de produção;
- tomar decisões de release sem confirmação humana.

## Regras de negócio

### Criação e revisão

- título, problema, solução proposta, responsável e nível de risco são obrigatórios;
- cada especificação precisa de ao menos um critério de aceite;
- apenas uma especificação em `DRAFT` pode ser enviada para revisão;
- uma aprovação exige comentário da pessoa revisora;
- uma devolução exige justificativa e retorna a especificação para `DRAFT`.

### Implementação e release

- apenas uma especificação `APPROVED` pode iniciar implementação;
- a decisão de release exige todos os critérios verificados;
- os gates `BACKEND_TESTS`, `FRONTEND_TESTS`, `QUALITY`, `SECURITY` e `RELEASE_NOTES` precisam de estado `PASSED` e uma referência de evidência;
- a aplicação nunca infere um gate aprovado pela ausência de erro;
- uma especificação só muda para `RELEASED` após uma avaliação de prontidão aprovada.

### Agente e MCP

- o plano gerado deve referenciar os identificadores dos critérios de aceite;
- a resposta deve separar tarefas de backend, frontend, testes e documentação;
- ferramentas MCP são somente leitura no primeiro incremento;
- qualquer futura ferramenta de escrita deverá exigir confirmação humana explícita.

## Critérios de aceite

| ID | Comportamento esperado |
|---|---|
| AC-01 | Uma especificação válida é criada em `DRAFT` com chave legível e histórico inicial. |
| AC-02 | Uma especificação sem critério de aceite recebe resposta `400` no formato Problem Details. |
| AC-03 | O fluxo aceita apenas transições permitidas e responde `409` para transição inválida. |
| AC-04 | A devolução para `DRAFT` preserva a justificativa no histórico. |
| AC-05 | A prontidão lista critérios ou gates pendentes em vez de retornar apenas `false`. |
| AC-06 | A release é bloqueada enquanto existir gate sem evidência ou critério não verificado. |
| AC-07 | O plano de implementação referencia cada critério de aceite da especificação. |
| AC-08 | A interface Angular permite criar, listar, revisar e acompanhar a prontidão. |
| AC-09 | A API exige chave de acesso fora dos endpoints de health. |
| AC-10 | As ferramentas MCP consultam especificações e prontidão sem modificar dados. |

## Cenários de falha

- duas pessoas atualizam a mesma especificação: a versão otimista impede sobrescrita silenciosa;
- um cliente repete uma transição: a API devolve conflito e mantém o histórico original;
- uma evidência aponta para uma string vazia: o gate continua pendente;
- o planejador local recebe uma especificação incompleta: ele explica quais dados faltam;
- o banco fica indisponível: health readiness falha e a API não simula sucesso.

## Métricas do incremento

- cobertura mínima de 85% no backend;
- testes dos componentes e serviços críticos do Angular;
- zero violações bloqueantes no Checkstyle e SpotBugs;
- SBOM gerado no build do backend;
- tempo e resultado das avaliações de prontidão expostos ao Prometheus.
