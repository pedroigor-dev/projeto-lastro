# Working agreement

Lastro is developed from an approved specification, not from an isolated prompt. Before changing code:

1. read the active file under `specs/` and quote the acceptance criterion being addressed;
2. inspect the relevant ADRs and preserve the domain boundaries;
3. propose the smallest implementation plan that covers the criterion;
4. add executable evidence and run the quality command for the affected stack;
5. update the specification or create an ADR when the implementation changes a decision.

Never mark a gate as passed without a reproducible evidence reference. MCP tools in this repository are read-only by design.
