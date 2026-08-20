package dev.pedrocosta.vertex.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("vertex.security")
public record ApiKeyProperties(String apiKey) {

    public ApiKeyProperties {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("vertex.security.api-key is required");
        }
    }
}
