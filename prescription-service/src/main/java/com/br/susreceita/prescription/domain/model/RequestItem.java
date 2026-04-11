package com.br.susreceita.prescription.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "request_item", schema = "prescription")
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID requestItemId;

    // Relacionamento N:1 - Muitos itens pertencem a uma requisição
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Request request;

    @Column(name = "raw_name")
    private String name;

    @Column(name = "active_ingredient")
    private String ingredient;

    @Column(name = "dosage")
    private String dosage;

    public RequestItem() {
    }

    public RequestItem(UUID requestItemId, Request request, String name, String ingredient, String dosage) {
        this.requestItemId = requestItemId;
        this.request = request;
        this.name = name;
        this.ingredient = ingredient;
        this.dosage = dosage;
    }

    public UUID getRequestItemId() {
        return requestItemId;
    }

    public void setRequestItemId(UUID requestItemId) {
        this.requestItemId = requestItemId;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
}
