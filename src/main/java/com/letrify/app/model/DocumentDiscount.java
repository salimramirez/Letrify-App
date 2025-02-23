package com.letrify.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount; // Relación con discounts

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document; // Relación con documents

    @Column(name = "nominal_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal nominalValue; // Valor nominal antes del descuento

    @Column(name = "discount_rate", precision = 15, scale = 9, nullable = false)
    private BigDecimal discountRate; // Tasa de descuento aplicada (d%)

    @Column(name = "interest_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestAmount; // Total de intereses descontados

    @Column(name = "net_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal netValue; // Valor neto después del descuento de intereses

    @Column(name = "received_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal receivedValue; // Monto efectivamente recibido después de costos iniciales

    @Column(name = "delivered_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal deliveredValue; // Monto total entregado al final

    @Column(name = "tcea", precision = 15, scale = 9)
    private BigDecimal tcea; // TCEA individual del documento

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
}
