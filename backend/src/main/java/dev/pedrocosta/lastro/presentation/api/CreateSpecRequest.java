package dev.pedrocosta.lastro.presentation.api;

import dev.pedrocosta.lastro.application.CreateSpecCommand;
import dev.pedrocosta.lastro.domain.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateSpecRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 4000) String problem,
        @NotBlank @Size(max = 4000) String proposedSolution,
        @NotBlank @Size(max = 120) String owner,
        @NotNull RiskLevel riskLevel,
        @NotEmpty List<@NotBlank @Size(max = 500) String> acceptanceCriteria
) {
    public CreateSpecRequest {
        acceptanceCriteria = List.copyOf(acceptanceCriteria);
    }

    CreateSpecCommand toCommand() {
        return new CreateSpecCommand(
                title, problem, proposedSolution, owner, riskLevel, acceptanceCriteria);
    }
}
