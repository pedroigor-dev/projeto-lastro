package dev.pedrocosta.lastro.application;

import dev.pedrocosta.lastro.application.port.ImplementationPlanner;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanningService {

    private final ChangeSpecService specifications;
    private final ImplementationPlanner planner;

    public PlanningService(ChangeSpecService specifications, ImplementationPlanner planner) {
        this.specifications = specifications;
        this.planner = planner;
    }

    @Transactional(readOnly = true)
    public ImplementationPlan plan(UUID specificationId) {
        return planner.plan(specifications.find(specificationId));
    }
}
