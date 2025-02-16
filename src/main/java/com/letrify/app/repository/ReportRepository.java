package com.letrify.app.repository;

import com.letrify.app.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Buscar todos los reportes generados por un usuario específico
    List<Report> findByUserId(Long userId);

    // Buscar reportes por tipo de reporte
    List<Report> findByReportType(String reportType);

    // Buscar reportes generados en un rango de fechas
    List<Report> findByGeneratedAtBetween(Date startDate, Date endDate);

    // Verificar si existe un reporte con un tipo específico para un usuario
    boolean existsByUserIdAndReportType(Long userId, String reportType);
}
