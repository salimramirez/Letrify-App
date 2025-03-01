package com.letrify.app.repository;

import com.letrify.app.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Método para obtener los reportes en orden descendente por fecha de generación
    List<Report> findAllByOrderByReportDateDesc();
}
