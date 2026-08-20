package dev.pedrocosta.vertex.application;

import dev.pedrocosta.vertex.domain.RiskLevel;
import dev.pedrocosta.vertex.domain.SpecStatus;
import java.time.Instant;
import java.util.UUID;

public record SpecSummary(
        UUID id,
        String key,
        String title,
        String owner,
        RiskLevel riskLevel,
        SpecStatus status,
        Instant updatedAt
) {
}
