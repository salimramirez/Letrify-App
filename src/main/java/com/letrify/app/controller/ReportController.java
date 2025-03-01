package com.letrify.app.controller;

import com.letrify.app.model.Report;
import com.letrify.app.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Obtiene todos los reportes generados en orden descendente.
     *
     * @return Lista de reportes generados.
     */
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Obtiene un reporte específico por su ID.
     *
     * @param id ID del reporte.
     * @return El reporte si existe, o código 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    /**
     * Genera un nuevo reporte y lo guarda en la base de datos.
     *
     * @param request Objeto con el snapshot de las carteras en formato JSON.
     * @return El reporte generado con la ruta del PDF.
     */
    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(@RequestBody ReportRequest request) {
        Report newReport = reportService.generateAndSaveReport(request.getPortfolioSnapshot());
        return newReport != null ? ResponseEntity.ok(newReport) : ResponseEntity.status(500).build();
    }

    /**
     * Endpoint para servir los archivos PDF de los reportes almacenados en la carpeta 'reports/'.
     *
     * @param fileName Nombre del archivo PDF.
     * @return El archivo PDF si existe, o un código 404 si no se encuentra.
     */
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewReport(@PathVariable String fileName) {
        try {
            // Ruta absoluta del archivo PDF
            Path filePath = Paths.get("reports/" + fileName).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Clase interna para recibir la solicitud de generación de reportes.
     */
    public static class ReportRequest {
        private String portfolioSnapshot;

        public String getPortfolioSnapshot() {
            return portfolioSnapshot;
        }

        public void setPortfolioSnapshot(String portfolioSnapshot) {
            this.portfolioSnapshot = portfolioSnapshot;
        }
    }
}
