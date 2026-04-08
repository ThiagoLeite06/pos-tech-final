package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.domain.model.Prescription;

public interface PrescriptionEventPublisherPort {
    void publishPrescriptionRequest(Prescription prescription);
    void publishPrescriptionStatus(Prescription prescription);
}
