package dev.pedrocosta.lastro.domain;

public final class InvalidTransitionException extends DomainException {

    private static final long serialVersionUID = 1L;

    public InvalidTransitionException(SpecStatus current, SpecStatus target) {
        super("Cannot transition a specification from " + current + " to " + target);
    }
}
