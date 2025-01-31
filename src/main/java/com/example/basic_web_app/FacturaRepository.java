package com.example.basic_web_app;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // Métodos adicionales si los necesitas
}
