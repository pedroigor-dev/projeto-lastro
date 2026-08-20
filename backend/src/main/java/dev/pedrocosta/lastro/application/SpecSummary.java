package dev.pedrocosta.lastro.application;

import dev.pedrocosta.lastro.domain.RiskLevel;
import dev.pedrocosta.lastro.domain.SpecStatus;
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
