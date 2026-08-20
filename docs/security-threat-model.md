# Threat model

## Protected assets

The service protects specifications, acceptance evidence, audit history and the API credential. A forged gate or an erased review reason is more damaging than temporary read unavailability because it can produce a false release decision.

## Main threats and controls

| Threat | Current control | Residual risk / next step |
|---|---|---|
| Unauthenticated API access | Constant-time API-key comparison; stateless requests | Replace shared key with OIDC and scoped roles before production |
| Cross-site requests | Explicit CORS origin for local Angular client | Configure allowed origins per deployed environment |
| Lost concurrent update | JPA optimistic locking | Return the current version in a richer conflict response |
| Fabricated release evidence | Evidence reference required for passed gates; audit trail | Validate CI signatures through provider APIs |
| Agent changes production state | MCP surface is read-only | Review every new MCP tool against ADR-0003 |
| Dependency compromise | Lockfile, SBOM, Dependabot, CI scans | Add image signing and admission policy |
| Secret disclosure | Environment-variable configuration | Use a secret manager and automatic rotation |

## Trust boundaries

The browser is untrusted. The backend owns lifecycle invariants and release readiness. PostgreSQL is reachable only by the backend network in the supplied Compose topology. CI evidence is a reference in this POC, not cryptographic proof.
