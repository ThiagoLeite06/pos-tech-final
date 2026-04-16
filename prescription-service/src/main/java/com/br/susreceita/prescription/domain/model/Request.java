package com.br.susreceita.prescription.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "request", schema = "prescription")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "patient_cpf")
    private String cpf;

    @Column(name = "patient_sus_card")
    private String numSusCard;

    @Enumerated(EnumType.STRING)
    private EvidenceStatus status;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "doctor_crm")
    private String doctorCrm;

    @Column(name = "prescription_date")
    private LocalDate prescriptionDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @Version
    @Column(name = "attempts_count")
    private Integer attempts = 0;

    // Relacionamento 1:N - Uma requisição tem vários itens
    // mappedBy indica o nome do atributo na classe RequestItem
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestItem> items;

    public Request() {
    }

    public Request(UUID id, String cpf, String numSusCard, EvidenceStatus status, String fullName, String doctorCrm, LocalDate prescriptionDate, LocalDateTime createdAt, LocalDateTime updateAt, Integer attempts, List<RequestItem> items) {
        this.id = id;
        this.cpf = cpf;
        this.numSusCard = numSusCard;
        this.status = status;
        this.fullName = fullName;
        this.doctorCrm = doctorCrm;
        this.prescriptionDate = prescriptionDate;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.attempts = attempts;
        this.items = items;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID requestId) {
        this.id = requestId;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNumSusCard() {
        return numSusCard;
    }

    public void setNumSusCard(String numSusCard) {
        this.numSusCard = numSusCard;
    }

    public EvidenceStatus getStatus() {
        return status;
    }

    public void setStatus(EvidenceStatus status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDoctorCrm() {
        return doctorCrm;
    }

    public void setDoctorCrm(String doctorCrm) {
        this.doctorCrm = doctorCrm;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public List<RequestItem> getItems() {
        return items;
    }

    public void setItems(List<RequestItem> items) {
        this.items = items;
    }
}
