package com.letrify.app.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PortfolioDocumentId implements Serializable {
    
    private Long portfolioId;
    private Long documentId;

    // Constructor vacío
    public PortfolioDocumentId() {}

    // Constructor con parámetros
    public PortfolioDocumentId(Long portfolioId, Long documentId) {
        this.portfolioId = portfolioId;
        this.documentId = documentId;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioDocumentId that = (PortfolioDocumentId) o;
        return Objects.equals(portfolioId, that.portfolioId) &&
               Objects.equals(documentId, that.documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portfolioId, documentId);
    }
}
