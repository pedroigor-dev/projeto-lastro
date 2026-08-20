---
name: spec-review
description: Review a Vertex change specification before implementation or release.
---

# Specification review

Use this skill when a change is about to enter review, implementation or release.

## Review procedure

1. Read the complete specification and its linked ADRs.
2. Check that the problem describes an observable situation and does not assume a solution.
3. Check every acceptance criterion for a single, testable behavior.
4. Identify security, migration, rollback and observability risks.
5. Map implementation tasks and tests back to criterion identifiers.
6. Query `vertex_release_readiness` through MCP before recommending release.
7. Report blockers plainly. Do not pass a gate or mutate a specification.

## Output

Return: decision, concrete blockers, criterion-to-evidence map, unresolved risks and the next review action. Avoid generic advice.
