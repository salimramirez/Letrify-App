package com.letrify.app.repository;

import com.letrify.app.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
// import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    // Buscar todos los descuentos asociados a una cartera específica
    List<Discount> findByPortfolioId(Long portfolioId);

    // Buscar todos los descuentos asociados a un banco específico
    List<Discount> findByBankId(Long bankId);

    // Buscar descuentos con una TCEA superior a un valor dado
    List<Discount> findByTceaGreaterThan(BigDecimal tcea);

    // Verificar si ya existe un descuento para una combinación específica de cartera y banco
    boolean existsByPortfolioIdAndBankId(Long portfolioId, Long bankId);

    // Buscar descuentos por rango de fecha de creación
    List<Discount> findByCreatedAtBetween(Date startDate, Date endDate);
}
