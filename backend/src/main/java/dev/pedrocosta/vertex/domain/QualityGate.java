package dev.pedrocosta.vertex.domain;

import java.time.Instant;
import java.util.Objects;

public record QualityGate(
        GateType type,
        GateStatus status,
        String evidenceReference,
        Instant updatedAt
) {
    public QualityGate {
        Objects.requireNonNull(type, "Gate type is required");
        Objects.requireNonNull(status, "Gate status is required");
        Objects.requireNonNull(updatedAt, "Gate update time is required");
        evidenceReference = evidenceReference == null ? null : evidenceReference.trim();
        if (status == GateStatus.PASSED && (evidenceReference == null || evidenceReference.isBlank())) {
            throw new DomainException("A passed gate requires an evidence reference");
        }
    }
}
