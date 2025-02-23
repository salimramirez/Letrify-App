package com.letrify.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "bank_fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    @JsonIgnoreProperties({"portfolios", "createdAt", "updatedAt"})
    private Bank bank;  // Relación con la entidad Bank

    @Column(name = "fee_name", nullable = false, length = 255)
    private String feeName;  // Nombre del tipo de costo (Ej: "Comisión fija", "Gasto administrativo")

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;  // Tipo de costo (FIJO o PORCENTUAL)

    @Column(name = "fee_amount", nullable = false, precision = 12, scale = 6)
    private BigDecimal feeAmount;  // Monto fijo o porcentaje aplicado

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_timing", nullable = false)
    private FeeTiming feeTiming;  // Momento en que se aplica (INICIAL o FINAL)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enum para FeeType (FIJO o PORCENTUAL)
    public enum FeeType {
        FIJO, 
        PORCENTUAL;
    }

    // Enum para FeeTiming (INICIAL o FINAL)
    public enum FeeTiming {
        INICIAL, 
        FINAL;
    }
}
