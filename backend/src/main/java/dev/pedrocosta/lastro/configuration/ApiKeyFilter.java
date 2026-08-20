package dev.pedrocosta.lastro.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

final class ApiKeyFilter extends OncePerRequestFilter {

    static final String HEADER = "X-API-Key";
    private final byte[] expected;

    ApiKeyFilter(String expected) {
        this.expected = expected.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String provided = request.getHeader(HEADER);
        byte[] candidate = provided == null
                ? new byte[0]
                : provided.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, candidate)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getWriter().write("""
                    {"type":"about:blank","title":"Unauthorized","status":401,
                    "detail":"A valid X-API-Key header is required"}
                    """);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator/health");
    }
}
