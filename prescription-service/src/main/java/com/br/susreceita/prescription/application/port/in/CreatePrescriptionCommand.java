package com.br.susreceita.prescription.application.port.in;

import java.util.UUID;

public record CreatePrescriptionCommand(
    UUID requestId,
    String fullName,
    String cpf,
    String numSusCard,
    String file,
    String mimeType
) {}
