package dev.pedrocosta.lastro.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.pedrocosta.lastro.application.port.ChangeSpecRepository;
import dev.pedrocosta.lastro.domain.ChangeSpec;
import dev.pedrocosta.lastro.domain.GateStatus;
import dev.pedrocosta.lastro.domain.GateType;
import dev.pedrocosta.lastro.domain.RiskLevel;
import dev.pedrocosta.lastro.domain.SpecStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeSpecServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-20T12:00:00Z");
    private InMemoryRepository repository;
    private ChangeSpecService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository();
        service = new ChangeSpecService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void createsListsAndFindsSpecification() {
        SpecDetails created = create("Release evidence");
        create("A second specification");

        assertThat(created.key()).startsWith("LST-");
        assertThat(service.get(created.id()).title()).isEqualTo("Release evidence");
        assertThat(service.list()).extracting(SpecSummary::title)
                .containsExactly("Release evidence", "A second specification");
    }

    @Test
    void raisesSpecificErrorForUnknownSpecification() {
        UUID missingId = UUID.randomUUID();

        assertThatThrownBy(() -> service.get(missingId))
                .isInstanceOf(SpecNotFoundException.class)
                .hasMessageContaining(missingId.toString());
    }

    @Test
    void coordinatesCompleteLifecycle() {
        SpecDetails created = create("Release evidence");
        UUID id = created.id();
        service.submit(id, "Pedro");
        service.approve(id, "Reviewer", "Reviewed");
        service.startImplementation(id, "Pedro");
        UUID criterionId = service.get(id).acceptanceCriteria().getFirst().id();
        service.verifyCriterion(id, criterionId, true, "Pedro");
        for (GateType gate : GateType.values()) {
            service.recordGate(id, gate, GateStatus.PASSED, "ci/" + gate, "Pipeline");
        }

        assertThat(service.readiness(id).ready()).isTrue();
        assertThat(service.release(id, "Release manager").status())
                .isEqualTo(SpecStatus.RELEASED);
    }

    @Test
    void coordinatesReturnToDraft() {
        UUID id = create("Review me").id();
        service.submit(id, "Pedro");

        assertThat(service.returnToDraft(id, "Reviewer", "Add rollback detail").status())
                .isEqualTo(SpecStatus.DRAFT);
    }

    @Test
    void planningServiceDelegatesUsingStoredSpecification() {
        UUID id = create("Plan me").id();
        PlanningService planning = new PlanningService(
                service,
                specification -> new ImplementationPlan(
                        specification.id(), "test-planner", List.of())
        );

        assertThat(planning.plan(id).generatedBy()).isEqualTo("test-planner");
    }

    private SpecDetails create(String title) {
        return service.create(new CreateSpecCommand(
                title,
                "Release decisions lack traceability",
                "Link decisions to executable evidence",
                "Pedro Igor",
                RiskLevel.MEDIUM,
                List.of("The readiness endpoint lists blockers")
        ));
    }

    private static final class InMemoryRepository implements ChangeSpecRepository {
        private final Map<UUID, ChangeSpec> specifications = new LinkedHashMap<>();

        @Override
        public ChangeSpec save(ChangeSpec specification) {
            specifications.put(specification.id(), specification);
            return specification;
        }

        @Override
        public Optional<ChangeSpec> findById(UUID id) {
            return Optional.ofNullable(specifications.get(id));
        }

        @Override
        public List<ChangeSpec> findAll() {
            return new ArrayList<>(specifications.values());
        }
    }
}
