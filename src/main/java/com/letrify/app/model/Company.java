package com.letrify.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID único autoincremental

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;  // Relación con la tabla users

    @Column(name = "business_name", nullable = false, unique = true) // Agregar unique=true si aún no está
    private String businessName; // <-- Debe coincidir con el repositorio

    @Column(name = "ruc", nullable = false, unique = true)
    private String ruc;

    @Column(name = "fiscal_address")
    private String fiscalAddress;

    @Column(name = "industry")
    private String industry;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getFiscalAddress() {
        return fiscalAddress;
    }

    public void setFiscalAddress(String fiscalAddress) {
        this.fiscalAddress = fiscalAddress;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Company() {
        
    }
    
}
