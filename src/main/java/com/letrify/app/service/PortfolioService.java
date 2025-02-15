package com.letrify.app.service;

import com.letrify.app.model.Portfolio;
import com.letrify.app.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    // Crear una nueva cartera
    public Portfolio createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    // Actualizar una cartera existente
    public Optional<Portfolio> updatePortfolio(Long id, Portfolio updatedPortfolio) {
        return portfolioRepository.findById(id).map(portfolio -> {
            portfolio.setPortfolioName(updatedPortfolio.getPortfolioName());
            portfolio.setDescription(updatedPortfolio.getDescription());
            portfolio.setDiscountDate(updatedPortfolio.getDiscountDate());
            portfolio.setBank(updatedPortfolio.getBank());
            portfolio.setStatus(updatedPortfolio.getStatus());
            return portfolioRepository.save(portfolio);
        });
    }

    // Eliminar una cartera solo si existe
    public boolean deletePortfolio(Long id) {
        if (portfolioRepository.existsById(id)) { // Verifica si existe la cartera
            portfolioRepository.deleteById(id);
            return true;  // Eliminación exitosa
        }
        return false;  // La cartera no existe
    }

    // Obtener todas las carteras
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    // Obtener una cartera por su ID
    public Optional<Portfolio> getPortfolioById(Long id) {
        return portfolioRepository.findById(id);
    }

    // Calcular el monto total de una cartera (sumar todos los documentos asociados)
    public BigDecimal calculateTotalAmount(Portfolio portfolio) {
        // Placeholder: Deberemos implementar la lógica real una vez tengamos los documentos asociados
        return portfolio.getTotalAmount();  // De momento devuelve el valor almacenado
    }

    // Calcular la TCEA para una cartera (futuro)
    public BigDecimal calculateTCEA(Portfolio portfolio) {
        // Placeholder para el cálculo real del TCEA
        return BigDecimal.ZERO;
    }
}
