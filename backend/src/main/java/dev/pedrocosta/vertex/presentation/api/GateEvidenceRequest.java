package dev.pedrocosta.vertex.presentation.api;

import dev.pedrocosta.vertex.domain.GateStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GateEvidenceRequest(
        @NotNull GateStatus status,
        @Size(max = 500) String evidenceReference,
        @NotBlank @Size(max = 120) String actor
) {
}
