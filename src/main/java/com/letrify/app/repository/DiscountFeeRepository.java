package com.letrify.app.repository;

import com.letrify.app.model.DiscountFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DiscountFeeRepository extends JpaRepository<DiscountFee, Long> {

    // Buscar todos los gastos asociados a un descuento específico
    List<DiscountFee> findByDiscountId(Long discountId);

    // Buscar todos los gastos por tipo de gasto
    List<DiscountFee> findByFeeType(String feeType);

    // Buscar todos los gastos en un rango de fechas de creación
    List<DiscountFee> findByCreatedAtBetween(Date startDate, Date endDate);

    // Verificar si ya existe un gasto con un tipo específico para un descuento
    boolean existsByDiscountIdAndFeeType(Long discountId, String feeType);
}
