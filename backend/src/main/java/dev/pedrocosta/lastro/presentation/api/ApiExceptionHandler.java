package dev.pedrocosta.lastro.presentation.api;

import dev.pedrocosta.lastro.application.SpecNotFoundException;
import dev.pedrocosta.lastro.domain.DomainException;
import dev.pedrocosta.lastro.domain.InvalidTransitionException;
import jakarta.persistence.OptimisticLockException;
import java.net.URI;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", detail, "validation-error");
    }

    @ExceptionHandler(SpecNotFoundException.class)
    ProblemDetail notFound(SpecNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Specification not found",
                exception.getMessage(), "specification-not-found");
    }

    @ExceptionHandler({InvalidTransitionException.class, OptimisticLockException.class})
    ProblemDetail conflict(RuntimeException exception) {
        return problem(HttpStatus.CONFLICT, "Specification conflict",
                exception.getMessage(), "specification-conflict");
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail domain(DomainException exception) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Business rule rejected",
                exception.getMessage(), "business-rule-rejected");
    }

    private ProblemDetail problem(
            HttpStatus status, String title, String detail, String type
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://github.com/pedroigor-dev/projeto-lastro/problems/" + type));
        return problem;
    }
}
