package com.letrify.app.repository;

import com.letrify.app.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
// import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    // Buscar todos los descuentos asociados a una cartera específica
    List<Discount> findByPortfolio_Id(Long portfolioId);

    // Buscar todos los descuentos asociados a un banco específico
    List<Discount> findByBank_Id(Long bankId);

    // Buscar descuentos con una TCEA superior a un valor dado
    List<Discount> findByTceaGreaterThan(BigDecimal tcea);

    // Verificar si ya existe un descuento para una combinación específica de cartera y banco
    boolean existsByPortfolio_IdAndBank_Id(Long portfolioId, Long bankId);

    // Buscar descuentos por rango de fecha de descuento
    List<Discount> findByDiscountDateBetween(LocalDate startDate, LocalDate endDate);

    // Buscar descuentos por rango de fecha de creación
    List<Discount> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar todos los descuentos asociados a un tipo de cambio específico
    List<Discount> findByExchangeRate_Id(Long exchangeRateId);

}
