package com.example.basic_web_app;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "facturas") // Nombre de la tabla en plural y en snake_case
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria

    @Column(nullable = false)
    private Double monto; // Monto de la factura

    @Column(nullable = false)
    private String moneda; // Moneda (soles o dólares)

    @Column(nullable = false)
    private Double tasa; // Tasa de descuento (nominal o efectiva)

    @Column(nullable = false)
    private Date fecha_emision; // Fecha de emisión de la factura

    @Column(nullable = false)
    private Date fecha_vencimiento; // Fecha de vencimiento de la factura

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Relación con la tabla users
    private User user; // Usuario propietario de la factura

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Double getTasa() {
        return tasa;
    }

    public void setTasa(Double tasa) {
        this.tasa = tasa;
    }

    public Date getFecha_emision() {
        return fecha_emision;
    }

    public void setFecha_emision(Date fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    public Date getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(Date fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
