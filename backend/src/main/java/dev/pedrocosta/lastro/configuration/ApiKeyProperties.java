package dev.pedrocosta.lastro.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("lastro.security")
public record ApiKeyProperties(String apiKey) {

    public ApiKeyProperties {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("lastro.security.api-key is required");
        }
    }
}
