package dev.pedrocosta.vertex.application;

import java.util.List;
import java.util.UUID;

public record ImplementationPlan(UUID specificationId, String generatedBy, List<ImplementationTask> tasks) {
    public ImplementationPlan {
        tasks = List.copyOf(tasks);
    }
}
