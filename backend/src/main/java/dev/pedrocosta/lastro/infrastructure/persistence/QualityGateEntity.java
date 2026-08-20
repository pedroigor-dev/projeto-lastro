package dev.pedrocosta.lastro.infrastructure.persistence;

import dev.pedrocosta.lastro.domain.GateStatus;
import dev.pedrocosta.lastro.domain.GateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quality_gates")
class QualityGateEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spec_id", nullable = false)
    private ChangeSpecEntity specification;

    @Enumerated(EnumType.STRING)
    @Column(name = "gate_type", nullable = false, length = 30)
    private GateType gateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gate_status", nullable = false, length = 20)
    private GateStatus gateStatus;

    @Column(name = "evidence_reference", length = 500)
    private String evidenceReference;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected QualityGateEntity() {
    }

    QualityGateEntity(
            UUID id, GateType gateType, GateStatus gateStatus,
            String evidenceReference, Instant updatedAt
    ) {
        this.id = id;
        this.gateType = gateType;
        this.gateStatus = gateStatus;
        this.evidenceReference = evidenceReference;
        this.updatedAt = updatedAt;
    }

    void attach(ChangeSpecEntity specification) {
        this.specification = specification;
    }

    GateType gateType() { return gateType; }
    GateStatus gateStatus() { return gateStatus; }
    String evidenceReference() { return evidenceReference; }
    Instant updatedAt() { return updatedAt; }
}
