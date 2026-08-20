package dev.pedrocosta.vertex.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
class AuditEventEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spec_id", nullable = false)
    private ChangeSpecEntity specification;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Column(nullable = false, length = 120)
    private String actor;

    @Column(nullable = false, length = 1000)
    private String detail;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected AuditEventEntity() {
    }

    AuditEventEntity(UUID id, String eventType, String actor, String detail, Instant occurredAt) {
        this.id = id;
        this.eventType = eventType;
        this.actor = actor;
        this.detail = detail;
        this.occurredAt = occurredAt;
    }

    void attach(ChangeSpecEntity specification) {
        this.specification = specification;
    }

    UUID id() { return id; }
    String eventType() { return eventType; }
    String actor() { return actor; }
    String detail() { return detail; }
    Instant occurredAt() { return occurredAt; }
}
