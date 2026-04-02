package br.com.susreceita.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "refreshToken é obrigatório")
        String refreshToken
) {}
