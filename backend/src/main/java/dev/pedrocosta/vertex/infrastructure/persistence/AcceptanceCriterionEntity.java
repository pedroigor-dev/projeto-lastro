package dev.pedrocosta.vertex.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "acceptance_criteria")
class AcceptanceCriterionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spec_id", nullable = false)
    private ChangeSpecEntity specification;

    @Column(name = "criterion_order", nullable = false)
    private int criterionOrder;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private boolean verified;

    protected AcceptanceCriterionEntity() {
    }

    AcceptanceCriterionEntity(UUID id, int criterionOrder, String description, boolean verified) {
        this.id = id;
        this.criterionOrder = criterionOrder;
        this.description = description;
        this.verified = verified;
    }

    void attach(ChangeSpecEntity specification) {
        this.specification = specification;
    }

    UUID id() { return id; }
    String description() { return description; }
    boolean verified() { return verified; }
}
