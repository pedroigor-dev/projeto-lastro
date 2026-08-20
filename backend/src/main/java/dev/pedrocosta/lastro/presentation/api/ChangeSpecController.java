package dev.pedrocosta.lastro.presentation.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import dev.pedrocosta.lastro.application.ChangeSpecService;
import dev.pedrocosta.lastro.application.ImplementationPlan;
import dev.pedrocosta.lastro.application.PlanningService;
import dev.pedrocosta.lastro.application.SpecDetails;
import dev.pedrocosta.lastro.application.SpecSummary;
import dev.pedrocosta.lastro.domain.GateType;
import dev.pedrocosta.lastro.domain.ReadinessReport;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/specs")
public class ChangeSpecController {

    private final ChangeSpecService specifications;
    private final PlanningService planning;
    private final MeterRegistry meters;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "MeterRegistry is a process-wide collaborator managed by Spring"
    )
    public ChangeSpecController(
            ChangeSpecService specifications, PlanningService planning, MeterRegistry meters
    ) {
        this.specifications = specifications;
        this.planning = planning;
        this.meters = meters;
    }

    @PostMapping
    ResponseEntity<SpecDetails> create(@Valid @RequestBody CreateSpecRequest request) {
        SpecDetails created = specifications.create(request.toCommand());
        return ResponseEntity.created(URI.create("/api/v1/specs/" + created.id())).body(created);
    }

    @GetMapping
    List<SpecSummary> list() {
        return specifications.list();
    }

    @GetMapping("/{id}")
    SpecDetails get(@PathVariable UUID id) {
        return specifications.get(id);
    }

    @PostMapping("/{id}/submit")
    SpecDetails submit(@PathVariable UUID id, @Valid @RequestBody ActionRequest request) {
        return specifications.submit(id, request.actor());
    }

    @PostMapping("/{id}/approve")
    SpecDetails approve(@PathVariable UUID id, @Valid @RequestBody ActionRequest request) {
        return specifications.approve(id, request.actor(), request.comment());
    }

    @PostMapping("/{id}/return")
    SpecDetails returnToDraft(@PathVariable UUID id, @Valid @RequestBody ActionRequest request) {
        return specifications.returnToDraft(id, request.actor(), request.comment());
    }

    @PostMapping("/{id}/start")
    SpecDetails start(@PathVariable UUID id, @Valid @RequestBody ActionRequest request) {
        return specifications.startImplementation(id, request.actor());
    }

    @PutMapping("/{id}/criteria/{criterionId}")
    SpecDetails verifyCriterion(
            @PathVariable UUID id,
            @PathVariable UUID criterionId,
            @Valid @RequestBody CriterionVerificationRequest request
    ) {
        return specifications.verifyCriterion(
                id, criterionId, request.verified(), request.actor());
    }

    @PutMapping("/{id}/gates/{type}")
    SpecDetails recordGate(
            @PathVariable UUID id,
            @PathVariable GateType type,
            @Valid @RequestBody GateEvidenceRequest request
    ) {
        return specifications.recordGate(
                id, type, request.status(), request.evidenceReference(), request.actor());
    }

    @GetMapping("/{id}/readiness")
    ReadinessReport readiness(@PathVariable UUID id) {
        Timer.Sample sample = Timer.start(meters);
        ReadinessReport report = specifications.readiness(id);
        sample.stop(meters.timer("lastro.readiness.evaluation"));
        meters.counter("lastro.readiness.result", "ready", Boolean.toString(report.ready()))
                .increment();
        return report;
    }

    @PostMapping("/{id}/release")
    SpecDetails release(@PathVariable UUID id, @Valid @RequestBody ActionRequest request) {
        return specifications.release(id, request.actor());
    }

    @GetMapping("/{id}/plan")
    ImplementationPlan plan(@PathVariable UUID id) {
        return planning.plan(id);
    }
}
