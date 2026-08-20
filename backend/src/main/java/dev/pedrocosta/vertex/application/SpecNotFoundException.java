package dev.pedrocosta.vertex.application;

import java.util.UUID;

public final class SpecNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SpecNotFoundException(UUID id) {
        super("Specification not found: " + id);
    }
}
