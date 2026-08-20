package dev.pedrocosta.lastro.infrastructure.planning;

import dev.pedrocosta.lastro.application.ImplementationPlan;
import dev.pedrocosta.lastro.application.ImplementationTask;
import dev.pedrocosta.lastro.application.port.ImplementationPlanner;
import dev.pedrocosta.lastro.domain.ChangeSpec;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DeterministicImplementationPlanner implements ImplementationPlanner {

    @Override
    public ImplementationPlan plan(ChangeSpec specification) {
        List<String> criteria = specification.criteria().stream()
                .map(criterion -> criterion.id().toString())
                .toList();
        List<ImplementationTask> tasks = List.of(
                task("BACKEND", "Implement domain behavior and API contract",
                        "Keep invariants close to the aggregate and expose explicit failures", criteria),
                task("FRONTEND", "Build the specification workflow in Angular",
                        "Represent server state without duplicating release rules in the browser", criteria),
                task("TESTS", "Protect each acceptance criterion with executable evidence",
                        "A plan is only traceable when every criterion is covered", criteria),
                task("DOCUMENTATION", "Update diagrams, ADRs and release notes",
                        "Reviewers need the reasons behind the implementation", criteria)
        );
        return new ImplementationPlan(specification.id(), "deterministic-local-planner", tasks);
    }

    private ImplementationTask task(
            String area, String title, String rationale, List<String> criteria
    ) {
        return new ImplementationTask(area, title, rationale, criteria);
    }
}
