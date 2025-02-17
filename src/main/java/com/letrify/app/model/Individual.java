package com.letrify.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "individuals")
public class Individual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID único autoincremental

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;  // Relación con la tabla users

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "dni", nullable = false, unique = true)
    private String dni;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "address")
    private String address;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Individual() {
        
    }
    
}
