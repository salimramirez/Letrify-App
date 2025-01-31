package com.example.basic_web_app;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Cambiar el nombre de la tabla a "users"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @Column(nullable = false, unique = true)
    private String username; // Nombre de usuario (único)

    @Column(nullable = false)
    private String password; // Contraseña (encriptada)

    @Column(nullable = false, unique = true)
    private String email; // Correo electrónico (único)

    @Column(nullable = false)
    private String role; // Rol del usuario (ADMIN, USER)

    @Column(nullable = false)
    private Boolean enabled; // Estado del usuario (activo/inactivo)

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
