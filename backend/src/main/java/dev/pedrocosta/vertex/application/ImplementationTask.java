package dev.pedrocosta.vertex.application;

import java.util.List;

public record ImplementationTask(
        String area,
        String title,
        String rationale,
        List<String> acceptanceCriterionIds
) {
    public ImplementationTask {
        acceptanceCriterionIds = List.copyOf(acceptanceCriterionIds);
    }
}
