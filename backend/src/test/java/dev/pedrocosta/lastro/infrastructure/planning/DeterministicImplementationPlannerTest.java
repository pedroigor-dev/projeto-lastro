package dev.pedrocosta.lastro.infrastructure.planning;

import static org.assertj.core.api.Assertions.assertThat;

import dev.pedrocosta.lastro.application.ImplementationPlan;
import dev.pedrocosta.lastro.domain.ChangeSpec;
import dev.pedrocosta.lastro.domain.RiskLevel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeterministicImplementationPlannerTest {

    @Test
    void referencesEveryCriterionInEveryTask() {
        ChangeSpec specification = ChangeSpec.create(
                UUID.randomUUID(), "LST-12345678", "Title", "Problem", "Solution", "Pedro",
                RiskLevel.LOW, List.of("First criterion", "Second criterion"), Instant.EPOCH);

        ImplementationPlan plan = new DeterministicImplementationPlanner().plan(specification);

        assertThat(plan.generatedBy()).isEqualTo("deterministic-local-planner");
        assertThat(plan.tasks()).extracting(task -> task.area())
                .containsExactly("BACKEND", "FRONTEND", "TESTS", "DOCUMENTATION");
        assertThat(plan.tasks()).allSatisfy(task ->
                assertThat(task.acceptanceCriterionIds()).hasSize(2));
    }
}
