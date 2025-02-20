package com.letrify.app.repository;

import com.letrify.app.model.Portfolio;
import com.letrify.app.model.Portfolio.PortfolioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.letrify.app.model.Portfolio.CurrencyType;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // Buscar cartera por estado
    List<Portfolio> findByStatus(PortfolioStatus status);

    // Buscar cartera por rango de fecha de descuento
    List<Portfolio> findByDiscountDateBetween(LocalDate startDate, LocalDate endDate);

    // Buscar cartera por nombre (parcial o exacto, sin importar mayúsculas)
    List<Portfolio> findByPortfolioNameContainingIgnoreCase(String portfolioName);

    // Buscar todass lass cartera asociados a un banco específico
    List<Portfolio> findByBankId(Long bankId);

    // Nuevo método: Buscar carteras por moneda (PEN o USD)
    List<Portfolio> findByCurrency(CurrencyType currencyType);

    List<Portfolio> findByStatusAndCurrency(PortfolioStatus status, CurrencyType currencyType);
}
