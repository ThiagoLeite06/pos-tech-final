package com.br.susreceita.prescription.adapter.in.kafka.event;

public record EvidenceStatusEvent(
    String evidenceId,
    String status
    // TODO: Add other relevant event payload fields
) {}
