package com.br.susreceita.evidence.infrastructure.listener;

import org.springframework.kafka.annotation.KafkaListener;

public class PrescriptionListener {


    @KafkaListener(topics = "PRESCRIPTION.REQUEST", groupId = "prescription-service-group")
    public void consumeEvidenceStatus() {

    }
}
