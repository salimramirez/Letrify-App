package com.letrify.app.repository;

import com.letrify.app.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // Métodos adicionales si los necesitas
}
