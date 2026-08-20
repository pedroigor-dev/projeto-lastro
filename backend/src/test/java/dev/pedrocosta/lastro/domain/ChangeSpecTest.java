package dev.pedrocosta.lastro.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeSpecTest {

    private static final Instant NOW = Instant.parse("2026-08-20T12:00:00Z");
    private ChangeSpec specification;

    @BeforeEach
    void setUp() {
        specification = newSpecification(List.of("AC-01 displays release blockers"));
    }

    @Test
    void createsDraftWithCriteriaGatesAndAuditTrail() {
        assertThat(specification.status()).isEqualTo(SpecStatus.DRAFT);
        assertThat(specification.criteria()).hasSize(1);
        assertThat(specification.gates()).hasSize(GateType.values().length);
        assertThat(specification.history()).singleElement()
                .extracting(AuditEvent::type)
                .isEqualTo("CREATED");
    }

    @Test
    void rejectsSpecificationWithoutAcceptanceCriteria() {
        assertThatThrownBy(() -> newSpecification(List.of()))
                .isInstanceOf(DomainException.class)
                .hasMessage("At least one acceptance criterion is required");
    }

    @Test
    void followsReviewAndImplementationWorkflow() {
        specification.submitForReview("Pedro", NOW.plusSeconds(1));
        specification.approve("Reviewer", "Scope and risks reviewed", NOW.plusSeconds(2));
        specification.startImplementation("Pedro", NOW.plusSeconds(3));

        assertThat(specification.status()).isEqualTo(SpecStatus.IMPLEMENTING);
        assertThat(specification.history()).hasSize(4);
        assertThat(specification.updatedAt()).isEqualTo(NOW.plusSeconds(3));
    }

    @Test
    void returnsReviewedSpecificationToDraftWithReason() {
        specification.submitForReview("Pedro", NOW.plusSeconds(1));
        specification.returnToDraft("Reviewer", "Clarify the rollback plan", NOW.plusSeconds(2));

        assertThat(specification.status()).isEqualTo(SpecStatus.DRAFT);
        assertThat(specification.history().getLast().detail())
                .contains("Clarify the rollback plan");
    }

    @Test
    void rejectsApprovalWithoutCommentAndInvalidTransition() {
        assertThatThrownBy(() -> specification.approve("Reviewer", "", NOW))
                .isInstanceOf(DomainException.class)
                .hasMessage("Approval comment is required");
        assertThatThrownBy(() -> specification.startImplementation("Pedro", NOW))
                .isInstanceOf(InvalidTransitionException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void reportsEachPendingCriterionAndGate() {
        ReadinessReport report = specification.evaluateReadiness();

        assertThat(report.ready()).isFalse();
        assertThat(report.blockers())
                .hasSize(GateType.values().length + 2)
                .anyMatch(blocker -> blocker.contains("IMPLEMENTING"))
                .anyMatch(blocker -> blocker.contains("Acceptance criterion"));
    }

    @Test
    void releasesOnlyWhenEveryPieceOfEvidencePassed() {
        moveToImplementation();
        UUID criterionId = specification.criteria().getFirst().id();
        specification.verifyCriterion(criterionId, true, "Pedro", NOW.plusSeconds(4));
        for (GateType type : GateType.values()) {
            specification.recordGate(
                    type,
                    GateStatus.PASSED,
                    "https://ci.example/jobs/" + type.name().toLowerCase(),
                    "Pipeline",
                    NOW.plusSeconds(5)
            );
        }

        assertThat(specification.evaluateReadiness().ready()).isTrue();
        specification.release("Release manager", NOW.plusSeconds(6));
        assertThat(specification.status()).isEqualTo(SpecStatus.RELEASED);
    }

    @Test
    void preventsReleaseWhenEvidenceIsMissing() {
        moveToImplementation();

        assertThatThrownBy(() -> specification.release("Pedro", NOW.plusSeconds(4)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Specification is not ready for release");
    }

    @Test
    void validatesCriterionAndGateUpdates() {
        moveToImplementation();
        assertThatThrownBy(() -> specification.verifyCriterion(
                UUID.randomUUID(), true, "Pedro", NOW.plusSeconds(4)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not found");
        assertThatThrownBy(() -> specification.recordGate(
                GateType.QUALITY, GateStatus.PASSED, " ", "Pipeline", NOW.plusSeconds(4)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("evidence reference");
    }

    private void moveToImplementation() {
        specification.submitForReview("Pedro", NOW.plusSeconds(1));
        specification.approve("Reviewer", "Approved", NOW.plusSeconds(2));
        specification.startImplementation("Pedro", NOW.plusSeconds(3));
    }

    private ChangeSpec newSpecification(List<String> criteria) {
        return ChangeSpec.create(
                UUID.fromString("a5db2b30-ab9b-4fcb-81a1-283ed6e5ad3d"),
                "LST-A5DB2B30",
                "Trace release evidence",
                "Evidence is spread across tools",
                "Connect evidence to each specification",
                "Pedro",
                RiskLevel.MEDIUM,
                criteria,
                NOW
        );
    }
}
