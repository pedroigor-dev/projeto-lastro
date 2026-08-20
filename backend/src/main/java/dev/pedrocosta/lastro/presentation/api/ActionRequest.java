package dev.pedrocosta.lastro.presentation.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActionRequest(
        @NotBlank @Size(max = 120) String actor,
        @Size(max = 1000) String comment
) {
}
