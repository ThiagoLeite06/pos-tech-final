package com.br.susreceita.prescription.adapter.in.web.dto;

public record PrescriptionRequest(
    String patientId,
    String medicationDetails
    // TODO: Add other fields required for the request
) {}
