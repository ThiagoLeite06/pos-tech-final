package com.br.susreceita.evidence.infrastructure.service;

import com.br.susreceita.evidence.domain.gateway.EvidenceResultGateway;
import com.br.susreceita.evidence.domain.model.Evidence;
import com.br.susreceita.evidence.infrastructure.entity.EvidenceEntity;
import com.br.susreceita.evidence.infrastructure.presenter.PresenterEvidence;
import com.br.susreceita.evidence.infrastructure.repository.EvidenceRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EvidenceService implements EvidenceResultGateway {

    private final EvidenceRepository evidenceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EvidenceService(EvidenceRepository evidenceRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.evidenceRepository = evidenceRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void save(Evidence evidence) {
        EvidenceEntity toSave = PresenterEvidence.toEntity(evidence);
        evidenceRepository.save(toSave);
        kafkaTemplate.send("EVIDENCE.STATUS", toSave);
    }
}
