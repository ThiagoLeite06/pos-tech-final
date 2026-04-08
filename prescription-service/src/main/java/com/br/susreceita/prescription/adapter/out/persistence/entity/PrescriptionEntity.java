package com.br.susreceita.prescription.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
public class PrescriptionEntity {

    @Id
    private UUID id;
    private String patientId;
    private String medication;
    private String status;

    public PrescriptionEntity() {
    }

    public PrescriptionEntity(UUID id, String patientId, String medication, String status) {
        this.id = id;
        this.patientId = patientId;
        this.medication = medication;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getMedication() { return medication; }
    public void setMedication(String medication) { this.medication = medication; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
