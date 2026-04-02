package br.com.susreceita.auth.service;

import br.com.susreceita.auth.config.JwtProperties;
import br.com.susreceita.auth.domain.RefreshToken;
import br.com.susreceita.auth.domain.User;
import br.com.susreceita.auth.dto.LoginRequest;
import br.com.susreceita.auth.dto.LoginResponse;
import br.com.susreceita.auth.dto.LogoutRequest;
import br.com.susreceita.auth.dto.RefreshRequest;
import br.com.susreceita.auth.exception.AuthException;
import br.com.susreceita.auth.repository.RefreshTokenRepository;
import br.com.susreceita.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByCpf(request.cpf())
                .filter(User::isActive)
                .orElseThrow(() -> AuthException.unauthorized("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw AuthException.unauthorized("Credenciais inválidas");
        }

        String rawRefreshToken = jwtService.generateRefreshToken();
        String hashedRefreshToken = sha256Hex(rawRefreshToken);
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProperties.refreshExpirationMs() / 1000);

        refreshTokenRepository.save(new RefreshToken(user, hashedRefreshToken, expiresAt));

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponse(
                accessToken,
                rawRefreshToken,
                jwtProperties.expirationMs() / 1000,
                user.getRole().name()
        );
    }

    @Transactional
    public LoginResponse refresh(RefreshRequest request) {
        String hash = sha256Hex(request.refreshToken());

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> AuthException.unauthorized("Refresh token inválido"));

        if (!token.isValid()) {
            throw AuthException.unauthorized("Refresh token expirado ou revogado");
        }

        String accessToken = jwtService.generateAccessToken(token.getUser());

        return new LoginResponse(
                accessToken,
                request.refreshToken(),
                jwtProperties.expirationMs() / 1000,
                token.getUser().getRole().name()
        );
    }

    @Transactional
    public void logout(LogoutRequest request) {
        String hash = sha256Hex(request.refreshToken());
        refreshTokenRepository.findByTokenHash(hash)
                .ifPresent(RefreshToken::revoke);
        // no-op if the token does not exist — idempotent logout
    }

    // --- Helpers ---

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JDK spec — should never happen
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
