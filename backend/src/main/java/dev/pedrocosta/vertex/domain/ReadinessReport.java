package dev.pedrocosta.vertex.domain;

import java.util.List;

public record ReadinessReport(boolean ready, List<String> blockers) {

    public ReadinessReport {
        blockers = List.copyOf(blockers);
        if (ready && !blockers.isEmpty()) {
            throw new IllegalArgumentException("A ready report cannot contain blockers");
        }
    }
}
