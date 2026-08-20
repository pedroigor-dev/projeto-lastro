package dev.pedrocosta.vertex.application;

import dev.pedrocosta.vertex.domain.AcceptanceCriterion;
import dev.pedrocosta.vertex.domain.AuditEvent;
import dev.pedrocosta.vertex.domain.QualityGate;
import dev.pedrocosta.vertex.domain.RiskLevel;
import dev.pedrocosta.vertex.domain.SpecStatus;
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
