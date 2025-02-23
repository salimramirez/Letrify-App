package com.letrify.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates", uniqueConstraints = @UniqueConstraint(columnNames = {"currency_from", "currency_to", "exchange_date"}))
@Getter
@Setter
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_from", length = 3, nullable = false)
    private String currencyFrom = "USD"; // Siempre USD

    @Column(name = "currency_to", length = 3, nullable = false)
    private String currencyTo = "PEN"; // Siempre PEN

    @Column(name = "buy_rate", precision = 10, scale = 4, nullable = false)
    private BigDecimal buyRate; // Precio de compra

    @Column(name = "sell_rate", precision = 10, scale = 4, nullable = false)
    private BigDecimal sellRate; // Precio de venta

    @Column(name = "exchange_date", nullable = false)
    private LocalDate exchangeDate; // Fecha del tipo de cambio

    @Column(name = "source", nullable = false)
    private String source = "SUNAT"; // Fuente de datos

    @Column(name = "last_updated", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastUpdated;

    // Establecer valores predeterminados antes de insertar en la base de datos
    @PrePersist
    public void prePersist() {
        if (currencyFrom == null) currencyFrom = "USD";
        if (currencyTo == null) currencyTo = "PEN";
        if (source == null) source = "SUNAT";
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
