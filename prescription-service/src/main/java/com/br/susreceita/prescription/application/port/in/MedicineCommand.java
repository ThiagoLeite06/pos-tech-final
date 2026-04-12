package com.br.susreceita.prescription.application.port.in;

public record MedicineCommand(
    String name,
    String dosage
) {}