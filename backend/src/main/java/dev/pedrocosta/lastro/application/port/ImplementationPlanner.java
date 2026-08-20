package dev.pedrocosta.lastro.application.port;

import dev.pedrocosta.lastro.application.ImplementationPlan;
import dev.pedrocosta.lastro.domain.ChangeSpec;

public interface ImplementationPlanner {

    ImplementationPlan plan(ChangeSpec specification);
}
