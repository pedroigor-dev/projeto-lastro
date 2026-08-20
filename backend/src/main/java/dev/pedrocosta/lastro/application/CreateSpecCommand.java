package dev.pedrocosta.lastro.application;

import dev.pedrocosta.lastro.domain.RiskLevel;
import java.util.List;

public record CreateSpecCommand(
        String title,
        String problem,
        String proposedSolution,
        String owner,
        RiskLevel riskLevel,
        List<String> acceptanceCriteria
) {
    public CreateSpecCommand {
        acceptanceCriteria = List.copyOf(acceptanceCriteria);
    }
}
