package com.br.susreceita.prescription.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Prescription {
    private UUID id;
    private String patientId;
    private String status;
    private LocalDateTime createdAt;

    // TODO: Add domain logic, rich behaviors, and other attributes

    public Prescription(UUID id, String patientId, String status, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
