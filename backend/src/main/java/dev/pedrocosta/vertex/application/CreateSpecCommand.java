package dev.pedrocosta.vertex.application;

import dev.pedrocosta.vertex.domain.RiskLevel;
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
