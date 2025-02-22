package com.letrify.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;  // ENUM: FACTURA, LETRA

    @NotBlank
    @Size(max = 255)
    @Column(name = "document_number", unique = true, nullable = false)
    private String documentNumber;

    @NotBlank
    @Size(max = 255)
    @Column(name = "customer", nullable = false)
    private String customer;  // Nombre del cliente

    @Size(max = 255)
    private String description;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;  // ENUM: PEN, USD

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "discount_date", nullable = true)
    private LocalDate discountDate;  // Fecha en que se aplica el descuento

    @Column(name = "discount_days", nullable = true)
    private Integer discountDays;  // Días entre discountDate y dueDate

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;  // ENUM: PENDIENTE, EN_DESCUENTO, CANCELADO

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "individual_id")
    private Individual individual;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    @JsonBackReference  // Evita la serialización cíclica
    private Portfolio portfolio;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum DocumentType {
        FACTURA,
        LETRA;
    }

    public enum CurrencyType {
        PEN,
        USD;
    }

    // Enum para el estado del documento
    public enum DocumentStatus {
        PENDIENTE,
        EN_DESCUENTO,
        CANCELADO;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public DocumentType getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }
    
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    public String getCustomer() {
        return customer;
    }
    
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public CurrencyType getCurrency() {
        return currency;
    }
    
    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }
    
    public LocalDate getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getDiscountDate() {
        return discountDate;
    }

    public void setDiscountDate(LocalDate discountDate) {
        this.discountDate = discountDate;
        if (this.dueDate != null && discountDate != null) {
            this.discountDays = Math.toIntExact(java.time.temporal.ChronoUnit.DAYS.between(discountDate, dueDate));
        } else {
            this.discountDays = null;
        }
    }

    public Integer getDiscountDays() {
        return discountDays;
    }

    public void setDiscountDays(Integer discountDays) {
        this.discountDays = discountDays;
    }

    // MÉTODO PARA ACTUALIZAR AUTOMÁTICAMENTE discountDays
    public void updateDiscountDays() {
        if (this.discountDate != null && this.dueDate != null) {
            this.discountDays = Math.toIntExact(java.time.temporal.ChronoUnit.DAYS.between(discountDate, dueDate));
        } else {
            this.discountDays = null;
        }
    }

    public DocumentStatus getStatus() {
        return status;
    }
    
    public void setStatus(DocumentStatus status) {
        this.status = status;
    }
    
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public Long getCompanyId() {
        return company != null ? company.getId() : null;
    }

    public Long getIndividualId() {
        return individual != null ? individual.getId() : null;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
    
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }    

    public Long getPortfolioId() {
        return portfolio != null ? portfolio.getId() : null;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        updateDiscountDays();
    }

}
