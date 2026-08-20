package dev.pedrocosta.vertex.infrastructure.persistence;

import dev.pedrocosta.vertex.domain.RiskLevel;
import dev.pedrocosta.vertex.domain.SpecStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "change_specs")
class ChangeSpecEntity {

    @Id
    private UUID id;

    @Column(name = "spec_key", nullable = false, unique = true, length = 30)
    private String key;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "problem_text", nullable = false, length = 4000)
    private String problem;

    @Column(name = "proposed_solution", nullable = false, length = 4000)
    private String proposedSolution;

    @Column(name = "owner_name", nullable = false, length = 120)
    private String owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SpecStatus status;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "specification", cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("criterionOrder ASC")
    private List<AcceptanceCriterionEntity> criteria = new ArrayList<>();

    @OneToMany(mappedBy = "specification", cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("gateType ASC")
    private List<QualityGateEntity> gates = new ArrayList<>();

    @OneToMany(mappedBy = "specification", cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("occurredAt ASC")
    private List<AuditEventEntity> history = new ArrayList<>();

    protected ChangeSpecEntity() {
    }

    ChangeSpecEntity(
            UUID id, String key, String title, String problem, String proposedSolution,
            String owner, RiskLevel riskLevel, SpecStatus status, long version,
            Instant createdAt, Instant updatedAt
    ) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.problem = problem;
        this.proposedSolution = proposedSolution;
        this.owner = owner;
        this.riskLevel = riskLevel;
        this.status = status;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    void addCriterion(AcceptanceCriterionEntity criterion) {
        criteria.add(criterion);
        criterion.attach(this);
    }

    void addGate(QualityGateEntity gate) {
        gates.add(gate);
        gate.attach(this);
    }

    void addEvent(AuditEventEntity event) {
        history.add(event);
        event.attach(this);
    }

    UUID id() { return id; }
    String key() { return key; }
    String title() { return title; }
    String problem() { return problem; }
    String proposedSolution() { return proposedSolution; }
    String owner() { return owner; }
    RiskLevel riskLevel() { return riskLevel; }
    SpecStatus status() { return status; }
    long version() { return version; }
    Instant createdAt() { return createdAt; }
    Instant updatedAt() { return updatedAt; }
    List<AcceptanceCriterionEntity> criteria() { return List.copyOf(criteria); }
    List<QualityGateEntity> gates() { return List.copyOf(gates); }
    List<AuditEventEntity> history() { return List.copyOf(history); }
}
