package com.letrify.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

// import org.hibernate.usertype.UserType;

@Entity
@Table(name = "users") // Cambiar el nombre de la tabla a "users"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @Column(nullable = false, unique = true)
    private String email; // Correo electrónico (único)

    @Column(name = "phone_number", nullable = false, unique = true) // Nuevo campo de teléfono
    private String phoneNumber;

    @Column(nullable = false)
    private String password; // Contraseña cifrada

    @Column(nullable = false)
    private String role = "USER"; // Rol del usuario (ADMIN, USER) con valor por defecto

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType; // Tipo de usuario: empresa o persona

    @Column(nullable = false)
    private Boolean enabled = true; // Estado de la cuenta (activo/inactivo)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Fecha de creación de la cuenta

    @JsonIgnore // Evitar problemas de recursión infinita en JSON cuando se serializan respuestas en controladores REST
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Company company;

    @JsonIgnore // Evitar problemas de recursión infinita en JSON cuando se serializan respuestas en controladores REST
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Individual individual;

    // Enum para el tipo de usuario
    public enum UserType {
        COMPANY,
        INDIVIDUAL
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public boolean isCompany() {
        return this.userType == UserType.COMPANY;
    }
    
    public boolean isIndividual() {
        return this.userType == UserType.INDIVIDUAL;
    }    
}
