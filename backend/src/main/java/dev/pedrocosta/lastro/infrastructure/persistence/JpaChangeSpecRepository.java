package dev.pedrocosta.lastro.infrastructure.persistence;

import dev.pedrocosta.lastro.application.port.ChangeSpecRepository;
import dev.pedrocosta.lastro.domain.AcceptanceCriterion;
import dev.pedrocosta.lastro.domain.AuditEvent;
import dev.pedrocosta.lastro.domain.ChangeSpec;
import dev.pedrocosta.lastro.domain.GateType;
import dev.pedrocosta.lastro.domain.QualityGate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Repository;

@Repository
class JpaChangeSpecRepository implements ChangeSpecRepository {

    private final SpringDataChangeSpecRepository repository;

    JpaChangeSpecRepository(SpringDataChangeSpecRepository repository) {
        this.repository = repository;
    }

    @Override
    public ChangeSpec save(ChangeSpec specification) {
        return toDomain(repository.saveAndFlush(toEntity(specification)));
    }

    @Override
    public Optional<ChangeSpec> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ChangeSpec> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    private ChangeSpecEntity toEntity(ChangeSpec specification) {
        ChangeSpecEntity entity = new ChangeSpecEntity(
                specification.id(), specification.key(), specification.title(),
                specification.problem(), specification.proposedSolution(), specification.owner(),
                specification.riskLevel(), specification.status(), specification.version(),
                specification.createdAt(), specification.updatedAt());

        int order = 0;
        for (AcceptanceCriterion criterion : specification.criteria()) {
            entity.addCriterion(new AcceptanceCriterionEntity(
                    criterion.id(), order++, criterion.description(), criterion.verified()));
        }
        specification.gates().values().forEach(gate -> entity.addGate(new QualityGateEntity(
                UUID.nameUUIDFromBytes((specification.id() + ":" + gate.type())
                        .getBytes(StandardCharsets.UTF_8)),
                gate.type(), gate.status(), gate.evidenceReference(), gate.updatedAt())));
        specification.history().forEach(event -> entity.addEvent(new AuditEventEntity(
                event.id(), event.type(), event.actor(), event.detail(), event.occurredAt())));
        return entity;
    }

    private ChangeSpec toDomain(ChangeSpecEntity entity) {
        List<AcceptanceCriterion> criteria = entity.criteria().stream()
                .map(item -> new AcceptanceCriterion(item.id(), item.description(), item.verified()))
                .toList();
        Map<GateType, QualityGate> gates = new EnumMap<>(GateType.class);
        entity.gates().forEach(item -> gates.put(item.gateType(), new QualityGate(
                item.gateType(), item.gateStatus(), item.evidenceReference(), item.updatedAt())));
        List<AuditEvent> history = entity.history().stream()
                .map(item -> new AuditEvent(
                        item.id(), item.eventType(), item.actor(), item.detail(), item.occurredAt()))
                .toList();
        return ChangeSpec.restore(
                entity.id(), entity.key(), entity.title(), entity.problem(), entity.proposedSolution(),
                entity.owner(), entity.riskLevel(), entity.status(), criteria, gates, history,
                entity.createdAt(), entity.updatedAt(), entity.version());
    }
}
