package com.letrify.app.repository;

import com.letrify.app.model.DiscountFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountFeeRepository extends JpaRepository<DiscountFee, Long> {

    // Buscar todos los gastos asociados a un descuento específico
    List<DiscountFee> findByDiscount_Id(Long discountId);

    // Buscar todos los gastos por tipo de gasto
    List<DiscountFee> findByFeeType(DiscountFee.FeeType feeType);

    // Buscar todos los gastos en un rango de fechas de creación
    List<DiscountFee> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Verificar si ya existe un gasto con un tipo específico para un descuento
    boolean existsByDiscount_IdAndFeeType(Long discountId, DiscountFee.FeeType feeType);

    // Buscar todos los gastos según fee_timing (INICIAL o FINAL)
    List<DiscountFee> findByFeeTiming(DiscountFee.FeeTiming feeTiming);

    // Buscar todos los gastos de un descuento específico y su fee_timing
    List<DiscountFee> findByDiscount_IdAndFeeTiming(Long discountId, DiscountFee.FeeTiming feeTiming);
}
