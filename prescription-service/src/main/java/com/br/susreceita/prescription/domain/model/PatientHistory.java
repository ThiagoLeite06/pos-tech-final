package com.br.susreceita.prescription.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patient_history", schema = "prescription")
public class PatientHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "patient_cpf", length = 11, nullable = false)
    private String patientCpf;

    @Column(name = "active_ingredient")
    private String activeIngredient;

    @Column(name = "prescription_date")
    private LocalDate prescriptionDate;

    @Column(name = "consecutive_renewals")
    private Integer consecutiveRenewals = 0;

    public PatientHistory() {
    }

    public PatientHistory(UUID id, String patientCpf, String activeIngredient, LocalDate prescriptionDate, Integer consecutiveRenewals) {
        this.id = id;
        this.patientCpf = patientCpf;
        this.activeIngredient = activeIngredient;
        this.prescriptionDate = prescriptionDate;
        this.consecutiveRenewals = consecutiveRenewals;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPatientCpf() {
        return patientCpf;
    }

    public void setPatientCpf(String patientCpf) {
        this.patientCpf = patientCpf;
    }

    public String getActiveIngredient() {
        return activeIngredient;
    }

    public void setActiveIngredient(String activeIngredient) {
        this.activeIngredient = activeIngredient;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public Integer getConsecutiveRenewals() {
        return consecutiveRenewals;
    }

    public void setConsecutiveRenewals(Integer consecutiveRenewals) {
        this.consecutiveRenewals = consecutiveRenewals;
    }
}
