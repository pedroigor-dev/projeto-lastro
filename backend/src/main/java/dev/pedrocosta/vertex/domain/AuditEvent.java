package dev.pedrocosta.vertex.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record AuditEvent(UUID id, String type, String actor, String detail, Instant occurredAt) {

    public AuditEvent {
        Objects.requireNonNull(id, "Audit event id is required");
        type = requireText(type, "Audit event type is required");
        actor = requireText(actor, "Audit actor is required");
        detail = requireText(detail, "Audit detail is required");
        Objects.requireNonNull(occurredAt, "Audit time is required");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }
}
