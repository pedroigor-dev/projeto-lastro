package dev.pedrocosta.lastro.domain;

import java.util.Objects;
import java.util.UUID;

public record AcceptanceCriterion(UUID id, String description, boolean verified) {

    public AcceptanceCriterion {
        Objects.requireNonNull(id, "Criterion id is required");
        description = requireText(description, "Criterion description is required");
    }

    public AcceptanceCriterion verify(boolean value) {
        return new AcceptanceCriterion(id, description, value);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }
}
