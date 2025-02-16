package com.letrify.app.service;

import com.letrify.app.exception.ResourceNotFoundException;
import com.letrify.app.model.Report;
import com.letrify.app.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // Crear un nuevo reporte
    public Report createReport(Report report) {
        if (reportRepository.existsByUserIdAndReportType(report.getUser().getId(), report.getReportType())) {
            throw new IllegalArgumentException("El usuario ya tiene un reporte de tipo '" + report.getReportType() + "'.");
        }
        return reportRepository.save(report);
    }

    // Actualizar un reporte existente
    public Report updateReport(Long id, Report updatedReport) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));

        report.setReportType(updatedReport.getReportType());
        report.setFilePath(updatedReport.getFilePath());
        report.setDescription(updatedReport.getDescription());
        report.setGeneratedAt(updatedReport.getGeneratedAt());

        return reportRepository.save(report);
    }

    // Eliminar un reporte por su ID
    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        reportRepository.delete(report);
    }

    // Obtener todos los reportes
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Obtener un reporte por su ID
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
    }

    // Obtener todos los reportes de un usuario específico
    public List<Report> getReportsByUserId(Long userId) {
        return reportRepository.findByUserId(userId);
    }

    // Obtener reportes por tipo de reporte
    public List<Report> getReportsByReportType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }

    // Obtener reportes generados en un rango de fechas
    public List<Report> getReportsByDateRange(Date startDate, Date endDate) {
        return reportRepository.findByGeneratedAtBetween(startDate, endDate);
    }
}
