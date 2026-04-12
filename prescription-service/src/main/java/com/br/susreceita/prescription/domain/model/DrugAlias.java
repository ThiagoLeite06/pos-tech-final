package com.br.susreceita.prescription.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "drug_alias", schema = "medicine")
public class DrugAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "raw_name", nullable = false)
    private String rawName;

    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    public DrugAlias() {
    }

    public DrugAlias(UUID id, String rawName, String normalizedName) {
        this.id = id;
        this.rawName = rawName;
        this.normalizedName = normalizedName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }
}
