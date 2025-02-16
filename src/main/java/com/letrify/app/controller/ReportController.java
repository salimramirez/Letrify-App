package com.letrify.app.controller;

import com.letrify.app.model.Report;
import com.letrify.app.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Obtener todos los reportes de un usuario
    @GetMapping
    public ResponseEntity<List<Report>> getReportsByUserId(@PathVariable Long userId) {
        List<Report> reports = reportService.getReportsByUserId(userId);
        return ResponseEntity.ok(reports);
    }

    // Obtener un reporte específico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long userId, @PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    // Crear un nuevo reporte para un usuario
    @PostMapping
    public ResponseEntity<Report> createReport(@PathVariable Long userId, @RequestBody Report report) {
        report.getUser().setId(userId);  // Asociar el reporte al usuario correspondiente
        Report createdReport = reportService.createReport(report);
        return ResponseEntity.ok(createdReport);
    }

    // Actualizar un reporte existente
    @PutMapping("/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable Long userId, @PathVariable Long id, @RequestBody Report report) {
        Report updatedReport = reportService.updateReport(id, report);
        return ResponseEntity.ok(updatedReport);
    }

    // Eliminar un reporte
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long userId, @PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener reportes generados en un rango de fechas
    @GetMapping("/date-range")
    public ResponseEntity<List<Report>> getReportsByDateRange(
            @PathVariable Long userId,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        List<Report> reports = reportService.getReportsByDateRange(startDate, endDate);
        return ResponseEntity.ok(reports);
    }
}
