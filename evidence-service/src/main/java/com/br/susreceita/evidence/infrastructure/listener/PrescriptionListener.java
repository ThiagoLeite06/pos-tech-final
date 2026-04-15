package com.br.susreceita.evidence.infrastructure.listener;

import com.br.susreceita.evidence.application.RecognitionPrescriptionUsecase;
import com.br.susreceita.evidence.domain.model.EvidenceDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionListener {

    private final RecognitionPrescriptionUsecase recognitionPrescriptionUsecase;

    public PrescriptionListener(RecognitionPrescriptionUsecase recognitionPrescriptionUsecase) {
        this.recognitionPrescriptionUsecase = recognitionPrescriptionUsecase;
    }

    @KafkaListener(topics = "PRESCRIPTION.REQUEST", groupId = "prescription-service-group")
    public void consumeEvidenceStatus(EvidenceDto request) {
        recognitionPrescriptionUsecase.recognizePrescription(request);
    }
}
