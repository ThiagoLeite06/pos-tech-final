package com.br.susreceita.prescription.infrastructure.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String privateKey,
        String publicKey,
        long expirationMs,
        long refreshExpirationMs
) {}
