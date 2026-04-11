package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.domain.model.Request;

public interface PrescriptionEventPublisherPort {
    void publishPrescriptionRequest(Request request);
    void publishPrescriptionStatus(Request request);
}
