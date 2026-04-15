package com.br.susreceita.evidence.application;

import com.br.susreceita.evidence.domain.gateway.EvidenceResultGateway;
import com.br.susreceita.evidence.domain.gateway.OcrEngineGateway;
import com.br.susreceita.evidence.domain.model.Evidence;
import com.br.susreceita.evidence.domain.model.EvidenceDto;
import com.br.susreceita.evidence.domain.model.EvidenceStatus;
import com.br.susreceita.evidence.domain.model.RecognitionPrescription;
import org.springframework.stereotype.Component;

@Component
public class RecognitionPrescriptionUsecase {

    private final OcrEngineGateway ocrEngineGateway;
    private final EvidenceResultGateway evidenceResultGateway;

    public RecognitionPrescriptionUsecase(OcrEngineGateway ocrEngineGateway, EvidenceResultGateway evidenceResultGateway) {
        this.ocrEngineGateway = ocrEngineGateway;
        this.evidenceResultGateway = evidenceResultGateway;
    }

    public void recognizePrescription(EvidenceDto evidenceDto) {
        RecognitionPrescription extracted = ocrEngineGateway.recognize(evidenceDto.image64());
        Evidence evidence = mapToEvidence(evidenceDto, extracted);
        evidenceResultGateway.save(evidence);
    }
    
    private Evidence mapToEvidence(EvidenceDto evidenceDto, RecognitionPrescription extracted) {
        if (extracted.crm() == null || extracted.crm().isEmpty() ||
                extracted.prescriptionDate() == null || extracted.prescriptionDate().isEmpty() ||
                extracted.medicine() == null || extracted.medicine().isEmpty()) {
            return new Evidence(
                    evidenceDto.prescriptionId(),
                    extracted.crm(),
                    extracted.prescriptionDate(),
                    extracted.medicine(),
                    extracted.obs(),
                    EvidenceStatus.REJECTED
            );
        }

        return new Evidence(
                evidenceDto.prescriptionId(),
                extracted.crm(),
                extracted.prescriptionDate(),
                extracted.medicine(),
                extracted.obs(),
                EvidenceStatus.APPROVED
        );
    }
}
