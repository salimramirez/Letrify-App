package com.letrify.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_date", nullable = false, updatable = false)
    private LocalDateTime reportDate;

    @Column(name = "report_pdf_path", nullable = false, length = 500)
    private String reportPdfPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    private ReportStatus reportStatus;

    @Column(name = "portfolio_snapshot", columnDefinition = "JSON", nullable = false)
    private String portfolioSnapshot; // Se almacenará como JSON en formato String

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructor con parámetros
    public Report(String reportPdfPath, ReportStatus reportStatus, String portfolioSnapshot) {
        this.reportDate = LocalDateTime.now();
        this.reportPdfPath = reportPdfPath;
        this.reportStatus = reportStatus;
        this.portfolioSnapshot = portfolioSnapshot;
    }

    // Enumeración para el estado del reporte
    public enum ReportStatus {
        GENERADO, ERROR
    }
}