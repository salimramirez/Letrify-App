package com.letrify.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "portfolio_documents")
public class PortfolioDocument {

    @EmbeddedId
    private PortfolioDocumentId id;

    @ManyToOne
    @MapsId("portfolioId")
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne
    @MapsId("documentId")
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    // Constructor vacío
    public PortfolioDocument() {}

    // Constructor con parámetros
    public PortfolioDocument(Portfolio portfolio, Document document) {
        this.id = new PortfolioDocumentId(portfolio.getId(), document.getId());
        this.portfolio = portfolio;
        this.document = document;
    }

    // Getters y Setters
    public PortfolioDocumentId getId() {
        return id;
    }

    public void setId(PortfolioDocumentId id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

}
