package com.letrify.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_fees")
public class DiscountFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @ManyToOne
    @JoinColumn(name = "bank_fee_id")
    private BankFee bankFee; // Relación con bank_fees si aplica

    @Column(name = "fee_name", length = 255, nullable = false)
    private String feeName; // Nombre del costo

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType; // FIJO o PORCENTUAL

    @Column(name = "fee_amount", precision = 12, scale = 6, nullable = false)
    private BigDecimal feeAmount; // Monto fijo o porcentaje

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_timing", nullable = false)
    private FeeTiming feeTiming; // INICIAL o FINAL

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_source", nullable = false)
    private FeeSource feeSource; // BANCARIO o MANUAL

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum FeeType {
        FIJO, PORCENTUAL, RETENCION;
    }

    // Enum para fee_timing
    public enum FeeTiming {
        INICIAL, FINAL;
    }

    public enum FeeSource {
        BANCARIO, MANUAL;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public BankFee getBankFee() {
        return bankFee;
    }

    public void setBankFee(BankFee bankFee) {
        this.bankFee = bankFee;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public FeeTiming getFeeTiming() {
        return feeTiming;
    }

    public void setFeeTiming(FeeTiming feeTiming) {
        this.feeTiming = feeTiming;
    }

    public FeeSource getFeeSource() {
        return feeSource;
    }

    public void setFeeSource(FeeSource feeSource) {
        this.feeSource = feeSource;
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
}
