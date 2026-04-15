package com.br.susreceita.evidence.domain.gateway;

import com.br.susreceita.evidence.domain.model.RecognitionPrescription;

public interface OcrEngineGateway {

    RecognitionPrescription recognize(String image64);
}
