package com.letrify.app.service;

import com.letrify.app.exception.ResourceNotFoundException;
import com.letrify.app.model.Report;
import com.letrify.app.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // Crear un nuevo reporte asociado a un descuento
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    // Actualizar un reporte existente
    public Report updateReport(Long id, Report updatedReport) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));

        report.setBankName(updatedReport.getBankName());
        report.setDiscountDate(updatedReport.getDiscountDate());
        report.setRateType(updatedReport.getRateType());
        report.setRate(updatedReport.getRate());
        report.setRateDays(updatedReport.getRateDays());
        report.setCapitalizationDays(updatedReport.getCapitalizationDays());
        report.setTotalDiscountAmount(updatedReport.getTotalDiscountAmount());
        report.setInterestAmount(updatedReport.getInterestAmount());
        report.setTcea(updatedReport.getTcea());
        report.setExchangeRate(updatedReport.getExchangeRate());
        report.setReportGeneratedAt(updatedReport.getReportGeneratedAt());

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

    // Obtener reportes de un descuento específico
    public List<Report> getReportsByDiscountId(Long discountId) {
        return reportRepository.findByDiscountId(discountId);
    }

    // Obtener reportes generados en un rango de fechas
    public List<Report> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.findByReportGeneratedAtBetween(startDate, endDate);
    }
}
