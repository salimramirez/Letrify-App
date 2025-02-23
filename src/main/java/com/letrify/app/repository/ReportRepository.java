package com.letrify.app.repository;

import com.letrify.app.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Buscar todos los reportes generados para un descuento específico
    List<Report> findByDiscountId(Long discountId);

    // Buscar reportes generados en un rango de fechas
    List<Report> findByReportGeneratedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}