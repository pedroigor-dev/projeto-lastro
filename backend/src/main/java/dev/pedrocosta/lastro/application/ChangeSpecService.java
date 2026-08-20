package dev.pedrocosta.lastro.application;

import dev.pedrocosta.lastro.application.port.ChangeSpecRepository;
import dev.pedrocosta.lastro.domain.ChangeSpec;
import dev.pedrocosta.lastro.domain.GateStatus;
import dev.pedrocosta.lastro.domain.GateType;
import dev.pedrocosta.lastro.domain.ReadinessReport;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeSpecService {

    private final ChangeSpecRepository repository;
    private final Clock clock;

    public ChangeSpecService(ChangeSpecRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public SpecDetails create(CreateSpecCommand command) {
        UUID id = UUID.randomUUID();
        String key = "LST-" + id.toString().substring(0, 8).toUpperCase(Locale.ROOT);
        ChangeSpec specification = ChangeSpec.create(
                id,
                key,
                command.title(),
                command.problem(),
                command.proposedSolution(),
                command.owner(),
                command.riskLevel(),
                command.acceptanceCriteria(),
                Instant.now(clock)
        );
        return details(repository.save(specification));
    }

    @Transactional(readOnly = true)
    public List<SpecSummary> list() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(ChangeSpec::updatedAt).reversed())
                .map(this::summary)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpecDetails get(UUID id) {
        return details(find(id));
    }

    @Transactional
    public SpecDetails submit(UUID id, String actor) {
        return update(id, specification -> specification.submitForReview(actor, now()));
    }

    @Transactional
    public SpecDetails approve(UUID id, String actor, String comment) {
        return update(id, specification -> specification.approve(actor, comment, now()));
    }

    @Transactional
    public SpecDetails returnToDraft(UUID id, String actor, String reason) {
        return update(id, specification -> specification.returnToDraft(actor, reason, now()));
    }

    @Transactional
    public SpecDetails startImplementation(UUID id, String actor) {
        return update(id, specification -> specification.startImplementation(actor, now()));
    }

    @Transactional
    public SpecDetails verifyCriterion(UUID id, UUID criterionId, boolean verified, String actor) {
        return update(id, specification ->
                specification.verifyCriterion(criterionId, verified, actor, now()));
    }

    @Transactional
    public SpecDetails recordGate(
            UUID id, GateType type, GateStatus status, String evidence, String actor
    ) {
        return update(id, specification ->
                specification.recordGate(type, status, evidence, actor, now()));
    }

    @Transactional(readOnly = true)
    public ReadinessReport readiness(UUID id) {
        return find(id).evaluateReadiness();
    }

    @Transactional
    public SpecDetails release(UUID id, String actor) {
        return update(id, specification -> specification.release(actor, now()));
    }

    ChangeSpec find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new SpecNotFoundException(id));
    }

    private SpecDetails update(UUID id, SpecMutation mutation) {
        ChangeSpec specification = find(id);
        mutation.apply(specification);
        return details(repository.save(specification));
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private SpecSummary summary(ChangeSpec specification) {
        return new SpecSummary(
                specification.id(), specification.key(), specification.title(), specification.owner(),
                specification.riskLevel(), specification.status(), specification.updatedAt());
    }

    private SpecDetails details(ChangeSpec specification) {
        return new SpecDetails(
                specification.id(), specification.key(), specification.title(), specification.problem(),
                specification.proposedSolution(), specification.owner(), specification.riskLevel(),
                specification.status(), specification.criteria(),
                specification.gates().values().stream()
                        .sorted(Comparator.comparing(gate -> gate.type().name()))
                        .toList(),
                specification.history(), specification.createdAt(), specification.updatedAt(),
                specification.version());
    }

    @FunctionalInterface
    private interface SpecMutation {
        void apply(ChangeSpec specification);
    }
}
