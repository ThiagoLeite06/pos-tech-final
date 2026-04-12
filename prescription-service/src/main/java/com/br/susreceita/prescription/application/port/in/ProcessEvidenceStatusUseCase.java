package com.br.susreceita.prescription.application.port.in;

import java.sql.SQLDataException;

public interface ProcessEvidenceStatusUseCase {
    void processEvidenceStatus(ProcessEvidenceCommand command) throws SQLDataException;
}