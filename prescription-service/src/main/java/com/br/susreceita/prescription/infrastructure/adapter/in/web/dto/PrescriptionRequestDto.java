package com.br.susreceita.prescription.infrastructure.adapter.in.web.dto;

public record PrescriptionRequestDto(
    String fullName,
    String cpf,
    String numSusCard,
    String file,
    String mimeType
) {}
