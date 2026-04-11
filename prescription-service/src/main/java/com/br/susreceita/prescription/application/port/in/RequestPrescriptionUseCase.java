package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.Request;

public interface RequestPrescriptionUseCase {
    void requestPrescription(Request request);
}
