package br.com.susreceita.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding for jwt.* properties.
 * privateKey and publicKey are Base64-encoded DER bytes (PKCS8 / X509 respectively),
 * injected at runtime via JWT_PRIVATE_KEY / JWT_PUBLIC_KEY environment variables.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String privateKey,
        String publicKey,
        long expirationMs,
        long refreshExpirationMs
) {}
