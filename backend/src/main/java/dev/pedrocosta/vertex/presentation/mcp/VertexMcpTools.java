package dev.pedrocosta.vertex.presentation.mcp;

import dev.pedrocosta.vertex.application.ChangeSpecService;
import dev.pedrocosta.vertex.application.SpecDetails;
import dev.pedrocosta.vertex.application.SpecSummary;
import dev.pedrocosta.vertex.domain.ReadinessReport;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class VertexMcpTools {

    private final ChangeSpecService specifications;

    public VertexMcpTools(ChangeSpecService specifications) {
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
