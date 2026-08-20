package dev.pedrocosta.lastro.presentation.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriterionVerificationRequest(
        boolean verified,
        @NotBlank @Size(max = 120) String actor
) {
}
