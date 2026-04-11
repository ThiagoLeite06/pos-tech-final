package com.br.susreceita.prescription.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification", schema = "prescription")
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Relacionamento 1:1 com Request
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "id", unique = true)
    private Request request;

    @Column(name = "verification_code", unique = true, nullable = false, length = 50)
    private String verificationCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String signature;

    @Column(length = 10)
    private String algorithm = "RS256";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Verification() {
    }

    public Verification(UUID id, Request request, String verificationCode, String signature, String algorithm, LocalDateTime createdAt) {
        this.id = id;
        this.request = request;
        this.verificationCode = verificationCode;
        this.signature = signature;
        this.algorithm = algorithm;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
