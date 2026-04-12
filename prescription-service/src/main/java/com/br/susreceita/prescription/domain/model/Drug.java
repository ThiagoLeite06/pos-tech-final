package com.br.susreceita.prescription.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drug", schema = "medicine")
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "active_ingredient", nullable = false)
    private String activeIngredient;

    @Column(name = "regulatory_class", length = 2)
    private String regulatoryClass;

    @Column(name = "is_controlled", nullable = false)
    private boolean isControlled = false;

    @Column(name = "is_black_label", nullable = false)
    private boolean isBlackLabel = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Drug() {
    }

    public Drug(UUID id, String brandName, String activeIngredient, String regulatoryClass, boolean isControlled, boolean isBlackLabel, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.brandName = brandName;
        this.activeIngredient = activeIngredient;
        this.regulatoryClass = regulatoryClass;
        this.isControlled = isControlled;
        this.isBlackLabel = isBlackLabel;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getActiveIngredient() {
        return activeIngredient;
    }

    public void setActiveIngredient(String activeIngredient) {
        this.activeIngredient = activeIngredient;
    }

    public String getRegulatoryClass() {
        return regulatoryClass;
    }

    public void setRegulatoryClass(String regulatoryClass) {
        this.regulatoryClass = regulatoryClass;
    }

    public boolean isControlled() {
        return isControlled;
    }

    public void setControlled(boolean controlled) {
        isControlled = controlled;
    }

    public boolean isBlackLabel() {
        return isBlackLabel;
    }

    public void setBlackLabel(boolean blackLabel) {
        isBlackLabel = blackLabel;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
