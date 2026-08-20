package dev.pedrocosta.vertex.application.port;

import dev.pedrocosta.vertex.application.ImplementationPlan;
import dev.pedrocosta.vertex.domain.ChangeSpec;

public interface ImplementationPlanner {

    ImplementationPlan plan(ChangeSpec specification);
}
