package br.com.susreceita.auth.service;

import br.com.susreceita.auth.config.JwtProperties;
import br.com.susreceita.auth.domain.User;
import br.com.susreceita.auth.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT operations using JJWT 0.12.x with RS256 (asymmetric).
 *
 * <p>Access tokens are signed JWTs; refresh tokens are opaque UUID strings
 * stored hashed in the database.
 */
@Service
public class JwtService {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final JwtProperties jwtProperties;

    public JwtService(RSAPrivateKey privateKey,
                      RSAPublicKey publicKey,
                      JwtProperties jwtProperties) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Generates a signed RS256 access token for the given user.
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(jwtProperties.expirationMs());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("cpf", user.getCpf())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(privateKey)
                .compact();
    }

    /**
     * Generates an opaque random UUID to be used as a refresh token.
     * The raw value is returned to the client; a SHA-256 hash is stored in the database.
     */
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Validates the token signature and expiry. Throws {@link AuthException} on any failure.
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw AuthException.unauthorized("Token inválido ou expirado");
        }
    }

    public String extractSubject(String token) {
        return validateToken(token).getSubject();
    }

    public String extractCpf(String token) {
        return validateToken(token).get("cpf", String.class);
    }
}
