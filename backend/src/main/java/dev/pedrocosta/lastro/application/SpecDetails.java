package dev.pedrocosta.lastro.application;

import dev.pedrocosta.lastro.domain.AcceptanceCriterion;
import dev.pedrocosta.lastro.domain.AuditEvent;
import dev.pedrocosta.lastro.domain.QualityGate;
import dev.pedrocosta.lastro.domain.RiskLevel;
import dev.pedrocosta.lastro.domain.SpecStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SpecDetails(
        UUID id,
        String key,
        String title,
        String problem,
        String proposedSolution,
        String owner,
        RiskLevel riskLevel,
        SpecStatus status,
        List<AcceptanceCriterion> acceptanceCriteria,
        List<QualityGate> qualityGates,
        List<AuditEvent> history,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
    public SpecDetails {
        acceptanceCriteria = List.copyOf(acceptanceCriteria);
        qualityGates = List.copyOf(qualityGates);
        history = List.copyOf(history);
    }
}
