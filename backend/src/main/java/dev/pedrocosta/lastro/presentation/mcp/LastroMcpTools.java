package dev.pedrocosta.lastro.presentation.mcp;

import dev.pedrocosta.lastro.application.ChangeSpecService;
import dev.pedrocosta.lastro.application.SpecDetails;
import dev.pedrocosta.lastro.application.SpecSummary;
import dev.pedrocosta.lastro.domain.ReadinessReport;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class LastroMcpTools {

    private final ChangeSpecService specifications;

    public LastroMcpTools(ChangeSpecService specifications) {
        this.specifications = specifications;
    }

    @McpTool(description = "List change specifications without modifying them")
    public List<SpecSummary> listChangeSpecs() {
        return specifications.list();
    }

    @McpTool(description = "Get one change specification by UUID without modifying it")
    public SpecDetails getChangeSpec(
            @McpToolParam(description = "Specification UUID", required = true) String id
    ) {
        return specifications.get(UUID.fromString(id));
    }

    @McpTool(description = "Evaluate release readiness and return every active blocker")
    public ReadinessReport evaluateReleaseReadiness(
            @McpToolParam(description = "Specification UUID", required = true) String id
    ) {
        return specifications.readiness(UUID.fromString(id));
    }
}
