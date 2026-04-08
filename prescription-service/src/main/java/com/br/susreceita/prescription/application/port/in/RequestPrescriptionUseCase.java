package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.Prescription;

public interface RequestPrescriptionUseCase {
    void requestPrescription(Prescription prescription);
}
