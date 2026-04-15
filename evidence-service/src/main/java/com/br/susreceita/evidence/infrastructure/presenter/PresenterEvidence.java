package com.br.susreceita.evidence.infrastructure.presenter;

import com.br.susreceita.evidence.domain.model.Evidence;
import com.br.susreceita.evidence.infrastructure.entity.EvidenceEntity;

public class PresenterEvidence {
    public static EvidenceEntity toEntity(Evidence evidence) {
        return new EvidenceEntity();
    }
}
