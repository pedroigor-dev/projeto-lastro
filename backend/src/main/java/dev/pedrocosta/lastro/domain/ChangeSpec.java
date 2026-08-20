package dev.pedrocosta.lastro.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ChangeSpec {

    private final UUID id;
    private final String key;
    private final String title;
    private final String problem;
    private final String proposedSolution;
    private final String owner;
    private final RiskLevel riskLevel;
    private final Instant createdAt;
    private final List<AcceptanceCriterion> criteria;
    private final Map<GateType, QualityGate> gates;
    private final List<AuditEvent> history;
    private SpecStatus status;
    private Instant updatedAt;
    private long version;

    private ChangeSpec(
            UUID id,
            String key,
            String title,
            String problem,
            String proposedSolution,
            String owner,
            RiskLevel riskLevel,
            SpecStatus status,
            List<AcceptanceCriterion> criteria,
            Map<GateType, QualityGate> gates,
            List<AuditEvent> history,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = Objects.requireNonNull(id);
        this.key = requireText(key, "Specification key is required");
        this.title = requireText(title, "Title is required");
        this.problem = requireText(problem, "Problem is required");
        this.proposedSolution = requireText(proposedSolution, "Proposed solution is required");
        this.owner = requireText(owner, "Owner is required");
        this.riskLevel = Objects.requireNonNull(riskLevel);
        this.status = Objects.requireNonNull(status);
        this.criteria = new ArrayList<>(criteria);
        if (this.criteria.isEmpty()) {
            throw new DomainException("At least one acceptance criterion is required");
        }
        this.gates = new EnumMap<>(GateType.class);
        this.gates.putAll(gates);
        this.history = new ArrayList<>(history);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.version = version;
    }

    public static ChangeSpec create(
            UUID id,
            String key,
            String title,
            String problem,
            String proposedSolution,
            String owner,
            RiskLevel riskLevel,
            List<String> criterionDescriptions,
            Instant now
    ) {
        if (criterionDescriptions == null || criterionDescriptions.isEmpty()) {
            throw new DomainException("At least one acceptance criterion is required");
        }
        List<AcceptanceCriterion> criteria = criterionDescriptions.stream()
                .map(description -> new AcceptanceCriterion(UUID.randomUUID(), description, false))
                .toList();
        Map<GateType, QualityGate> gates = new EnumMap<>(GateType.class);
        for (GateType type : GateType.values()) {
            gates.put(type, new QualityGate(type, GateStatus.PENDING, null, now));
        }
        List<AuditEvent> history = List.of(event("CREATED", owner, "Specification created", now));
        return new ChangeSpec(id, key, title, problem, proposedSolution, owner, riskLevel,
                SpecStatus.DRAFT, criteria, gates, history, now, now, 0);
    }

    public static ChangeSpec restore(
            UUID id, String key, String title, String problem, String proposedSolution,
            String owner, RiskLevel riskLevel, SpecStatus status,
            List<AcceptanceCriterion> criteria, Map<GateType, QualityGate> gates,
            List<AuditEvent> history, Instant createdAt, Instant updatedAt, long version
    ) {
        return new ChangeSpec(id, key, title, problem, proposedSolution, owner, riskLevel,
                status, criteria, gates, history, createdAt, updatedAt, version);
    }

    public void submitForReview(String actor, Instant now) {
        transition(SpecStatus.DRAFT, SpecStatus.IN_REVIEW, actor, "Submitted for review", now);
    }

    public void approve(String actor, String comment, Instant now) {
        requireText(comment, "Approval comment is required");
        transition(SpecStatus.IN_REVIEW, SpecStatus.APPROVED, actor, comment, now);
    }

    public void returnToDraft(String actor, String reason, Instant now) {
        requireText(reason, "Return reason is required");
        transition(SpecStatus.IN_REVIEW, SpecStatus.DRAFT, actor, reason, now);
    }

    public void startImplementation(String actor, Instant now) {
        transition(SpecStatus.APPROVED, SpecStatus.IMPLEMENTING, actor, "Implementation started", now);
    }

    public void verifyCriterion(UUID criterionId, boolean verified, String actor, Instant now) {
        requireStatus(SpecStatus.IMPLEMENTING);
        int index = findCriterionIndex(criterionId);
        criteria.set(index, criteria.get(index).verify(verified));
        history.add(event("CRITERION_UPDATED", actor, criterionId + "=" + verified, now));
        updatedAt = now;
    }

    public void recordGate(
            GateType type, GateStatus gateStatus, String evidenceReference, String actor, Instant now
    ) {
        requireStatus(SpecStatus.IMPLEMENTING);
        gates.put(type, new QualityGate(type, gateStatus, evidenceReference, now));
        history.add(event("GATE_UPDATED", actor, type + "=" + gateStatus, now));
        updatedAt = now;
    }

    public ReadinessReport evaluateReadiness() {
        List<String> blockers = new ArrayList<>();
        if (status != SpecStatus.IMPLEMENTING) {
            blockers.add("Specification must be IMPLEMENTING");
        }
        criteria.stream()
                .filter(criterion -> !criterion.verified())
                .forEach(criterion -> blockers.add("Acceptance criterion pending: " + criterion.id()));
        gates.values().stream()
                .filter(gate -> gate.status() != GateStatus.PASSED)
                .forEach(gate -> blockers.add("Quality gate pending: " + gate.type()));
        return new ReadinessReport(blockers.isEmpty(), blockers);
    }

    public void release(String actor, Instant now) {
        if (!evaluateReadiness().ready()) {
            throw new DomainException("Specification is not ready for release");
        }
        transition(SpecStatus.IMPLEMENTING, SpecStatus.RELEASED, actor, "Release confirmed", now);
    }

    private int findCriterionIndex(UUID criterionId) {
        for (int index = 0; index < criteria.size(); index++) {
            if (criteria.get(index).id().equals(criterionId)) {
                return index;
            }
        }
        throw new DomainException("Acceptance criterion not found: " + criterionId);
    }

    private void transition(
            SpecStatus expected, SpecStatus target, String actor, String detail, Instant now
    ) {
        if (status != expected) {
            throw new InvalidTransitionException(status, target);
        }
        status = target;
        updatedAt = now;
        history.add(event("STATUS_CHANGED", actor, detail + " -> " + target, now));
    }

    private void requireStatus(SpecStatus expected) {
        if (status != expected) {
            throw new InvalidTransitionException(status, expected);
        }
    }

    private static AuditEvent event(String type, String actor, String detail, Instant now) {
        return new AuditEvent(UUID.randomUUID(), type, actor, detail, now);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }

    public UUID id() { return id; }
    public String key() { return key; }
    public String title() { return title; }
    public String problem() { return problem; }
    public String proposedSolution() { return proposedSolution; }
    public String owner() { return owner; }
    public RiskLevel riskLevel() { return riskLevel; }
    public SpecStatus status() { return status; }
    public List<AcceptanceCriterion> criteria() { return List.copyOf(criteria); }
    public Map<GateType, QualityGate> gates() { return Map.copyOf(gates); }
    public List<AuditEvent> history() { return List.copyOf(history); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
