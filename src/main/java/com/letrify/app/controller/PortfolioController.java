package com.letrify.app.controller;

import com.letrify.app.model.Portfolio;
import com.letrify.app.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // Obtener todas las carteras
    @GetMapping
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        List<Portfolio> portfolios = portfolioService.getAllPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    // Obtener una cartera por ID
    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long id) {
        Optional<Portfolio> portfolio = portfolioService.getPortfolioById(id);
        return portfolio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear una nueva cartera
    @PostMapping
    public ResponseEntity<?> createPortfolio(@RequestBody Portfolio portfolio) {
        try {
            System.out.println("📌 Datos Recibidos en el Backend: " + portfolio);

            // Verifica si el currency es null
            if (portfolio.getCurrency() == null) {
                System.out.println("⚠️ ERROR: currency es NULL en el backend.");
                return ResponseEntity.status(400).body("Error: La moneda (currency) no puede ser null.");
            }

            Portfolio createdPortfolio = portfolioService.createPortfolio(portfolio);
            return ResponseEntity.ok(createdPortfolio);
        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error exacto en la consola de Spring Boot
            return ResponseEntity.status(400).body("Error: No se pudo crear la cartera.");
        }
    }

    // Actualizar una cartera existente
    @PutMapping("/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Long id, @RequestBody Portfolio portfolio) {
        Optional<Portfolio> updatedPortfolio = portfolioService.updatePortfolio(id, portfolio);
        return updatedPortfolio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una cartera
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        if (portfolioService.deletePortfolio(id)) {
            return ResponseEntity.noContent().build();  // Eliminación exitosa
        } else {
            return ResponseEntity.notFound().build();   // No se encontró la cartera
        }
    }
}
