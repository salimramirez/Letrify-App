package com.letrify.app.service;

import com.letrify.app.model.Report;
import com.letrify.app.model.Report.ReportStatus;
import com.letrify.app.repository.ReportRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    private static final String REPORTS_DIR = "reports/";

    /**
     * Obtiene todos los reportes en orden descendente (el más reciente primero).
     */
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByReportDateDesc();
    }

    /**
     * Genera un nuevo reporte, crea un archivo PDF con la información de las carteras y lo almacena en la base de datos.
     *
     * @param portfolioSnapshot Estado actual de todas las carteras en formato JSON.
     * @return El reporte generado y guardado en la base de datos, o {@code null} si hubo un error.
     */
    @Transactional
    public Report generateAndSaveReport(String portfolioSnapshot) {
        try {
            // 1. Crear el directorio si no existe
            File dir = new File(REPORTS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. Definir la ruta del PDF
            String fileName = "reporte_" + System.currentTimeMillis() + ".pdf";
            String pdfPath = REPORTS_DIR + fileName;

            // 3. Generar el PDF
            generatePdf(portfolioSnapshot, pdfPath);

            // 4. Guardar en la base de datos
            Report report = new Report(pdfPath, ReportStatus.GENERADO, portfolioSnapshot);
            return reportRepository.save(report);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Genera un archivo PDF con el estado actual de las carteras.
     *
     * @param portfolioSnapshot JSON con la información de las carteras en el momento de la generación.
     * @param pdfPath Ruta donde se guardará el archivo PDF.
     * @throws Exception Si ocurre un error durante la generación del PDF.
     */
    private void generatePdf(String portfolioSnapshot, String pdfPath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
    
        // Agregar título principal
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Reporte de Estado de Carteras", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));
    
        if (portfolioSnapshot == null || portfolioSnapshot.isBlank()) {
            document.add(new Paragraph("No hay información de carteras disponible."));
            document.close();
            return;
        }
    
        try {
            JsonElement jsonElement = JsonParser.parseString(portfolioSnapshot);
            if (!jsonElement.isJsonArray()) {
                document.add(new Paragraph("Error: El JSON recibido no es un array de carteras."));
                document.close();
                return;
            }
    
            JsonArray portfolios = jsonElement.getAsJsonArray();
    
            for (JsonElement element : portfolios) {
                if (!element.isJsonObject()) continue;
                JsonObject portfolio = element.getAsJsonObject();
                
                // Agregar datos de la cartera
                agregarDatosCartera(document, portfolio);
    
                // Agregar documentos
                agregarDocumentos(document, portfolio);
    
                // Agregar detalles del descuento si existen
                agregarDetallesDescuento(document, portfolio);
    
                document.add(new Paragraph("------------------------------------------------------------"));
            }
        } catch (Exception e) {
            document.add(new Paragraph("Error al procesar la información del reporte."));
            e.printStackTrace();
        }
    
        document.close();
    }

    /**
     * Busca un reporte por su ID en la base de datos.
     *
     * @param reportId ID del reporte a buscar.
     * @return El reporte si existe, o {@code null} si no se encuentra.
     */
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId).orElse(null);
    }

    private void agregarDatosCartera(Document document, JsonObject portfolio) throws DocumentException {
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    
        String portfolioName = portfolio.has("portfolioName") ? portfolio.get("portfolioName").getAsString() : "Sin nombre";
        String currency = portfolio.has("currency") ? portfolio.get("currency").getAsString() : "Desconocido";
        String status = portfolio.has("status") ? portfolio.get("status").getAsString() : "Desconocido";
        int documentCount = portfolio.has("documentCount") ? portfolio.get("documentCount").getAsInt() : 0;
    
        document.add(new Paragraph("Cartera: " + portfolioName, subtitleFont));
        document.add(new Paragraph("Moneda: " + currency, textFont));
        document.add(new Paragraph("Estado: " + status, textFont));
        document.add(new Paragraph("Cantidad de documentos: " + documentCount, textFont));
        document.add(new Paragraph("\n"));
    }

    private void agregarDocumentos(Document document, JsonObject portfolio) throws DocumentException {
        if (portfolio.has("documents") && portfolio.get("documents").isJsonArray()) {
            JsonArray documents = portfolio.getAsJsonArray("documents");

            if (documents.size() > 0) {
                Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Font textFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

                document.add(new Paragraph("  📄 Documentos Asociados", subtitleFont));
                document.add(new Paragraph("\n"));

                // Crear tabla con columnas
                PdfPTable table = new PdfPTable(6); // 6 columnas
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.setSpacingAfter(10);

                // Definir encabezados
                String[] headers = {"N° Documento", "Tipo", "Cliente", "Monto", "F. Emisión", "F. Vencimiento"};
                for (String header : headers) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(header, subtitleFont));
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(headerCell);
                }

                // Llenar la tabla con los datos de los documentos
                for (JsonElement docElement : documents) {
                    if (!docElement.isJsonObject()) continue;
                    JsonObject doc = docElement.getAsJsonObject();

                    table.addCell(new PdfPCell(new Phrase(doc.has("documentNumber") ? doc.get("documentNumber").getAsString() : "N/A", textFont)));
                    table.addCell(new PdfPCell(new Phrase(doc.has("documentType") ? doc.get("documentType").getAsString() : "N/A", textFont)));
                    table.addCell(new PdfPCell(new Phrase(doc.has("customer") ? doc.get("customer").getAsString() : "N/A", textFont)));
                    table.addCell(new PdfPCell(new Phrase(doc.has("amount") ? "S/ " + doc.get("amount").getAsDouble() : "0.00", textFont)));
                    table.addCell(new PdfPCell(new Phrase(doc.has("issueDate") ? doc.get("issueDate").getAsString() : "N/A", textFont)));
                    table.addCell(new PdfPCell(new Phrase(doc.has("dueDate") ? doc.get("dueDate").getAsString() : "N/A", textFont)));
                }

                document.add(table);
            } else {
                document.add(new Paragraph("  ❌ No hay documentos en esta cartera.\n"));
            }
        }
    }

    private void agregarDetallesDescuento(Document document, JsonObject portfolio) throws DocumentException {
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font textFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    
        if (portfolio.has("discount") && portfolio.get("discount").isJsonObject()) {
            JsonObject discount = portfolio.getAsJsonObject("discount");
    
            String bank = discount.has("bank") ? discount.get("bank").getAsString() : "Desconocido";
            String discountDate = discount.has("discount_date") ? discount.get("discount_date").getAsString() : "No registrada";
            double tcea = discount.has("tcea") && !discount.get("tcea").isJsonNull() ? discount.get("tcea").getAsDouble() * 100 : 0.0;
            double interestAmount = discount.has("interest_amount") && !discount.get("interest_amount").isJsonNull() ? discount.get("interest_amount").getAsDouble() : 0.0;
            double totalCost = discount.has("total_cost") && !discount.get("total_cost").isJsonNull() ? discount.get("total_cost").getAsDouble() : 0.0;
    
            document.add(new Paragraph("  🔹 Descuento Aplicado", subtitleFont));
            document.add(new Paragraph("    Banco: " + bank, textFont));
            document.add(new Paragraph("    Fecha de Descuento: " + discountDate, textFont));
            document.add(new Paragraph("    TCEA: " + String.format("%.6f", tcea) + "%", textFont));
            document.add(new Paragraph("    Intereses totales: S/ " + String.format("%.2f", interestAmount), textFont));
            document.add(new Paragraph("    Costos totales: S/ " + String.format("%.2f", totalCost), textFont));
            document.add(new Paragraph("\n"));

            if (discount.has("document_discounts") && discount.get("document_discounts").isJsonArray() && discount.getAsJsonArray("document_discounts").size() > 0) {
                JsonArray documentDiscounts = discount.getAsJsonArray("document_discounts");
    
                document.add(new Paragraph("  📄 Descuentos por Documento", subtitleFont));
                document.add(new Paragraph("\n"));
    
                PdfPTable table = new PdfPTable(7); // 7 columnas
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.setSpacingAfter(10);
    
                // Definir encabezados
                String[] headers = {"N° Doc", "Valor Nominal", "Tasa Desc.", "Intereses", "Valor Neto", "Valor Recibido", "TCEA"};
                for (String header : headers) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(header, subtitleFont));
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(headerCell);
                }
    
                // Llenar la tabla con los datos de los descuentos por documento
                for (JsonElement docDiscountElement : documentDiscounts) {
                    if (!docDiscountElement.isJsonObject()) continue;
                    JsonObject docDiscount = docDiscountElement.getAsJsonObject();
    
                    table.addCell(new PdfPCell(new Phrase(docDiscount.has("document_number") && !docDiscount.get("document_number").isJsonNull() ? docDiscount.get("document_number").getAsString() : "N/A", textFont)));
                    table.addCell(new PdfPCell(new Phrase(docDiscount.has("nominal_value") && !docDiscount.get("nominal_value").isJsonNull() ? "S/ " + String.format("%.2f", docDiscount.get("nominal_value").getAsDouble()) : "0.00", textFont)));
                    table.addCell(new PdfPCell(new Phrase(docDiscount.has("discount_rate") && !docDiscount.get("discount_rate").isJsonNull() ? String.format("%.6f", docDiscount.get("discount_rate").getAsDouble() * 100) + "%" : "0.000000%", textFont)));
                    table.addCell(new PdfPCell(new Phrase(docDiscount.has("interest_amount") && !docDiscount.get("interest_amount").isJsonNull() ? "S/ " + String.format("%.2f", docDiscount.get("interest_amount").getAsDouble()) : "0.00", textFont)));
                    table.addCell(new PdfPCell(new Phrase(docDiscount.has("net_value") && !docDiscount.get("net_value").isJsonNull() ? "S/ " + String.format("%.2f", docDiscount.get("net_value").getAsDouble()) : "0.00", textFont)));
                    table.addCell(new PdfPCell(new Phrase(docDiscount.has("received_value") && !docDiscount.get("received_value").isJsonNull() ? "S/ " + String.format("%.2f", docDiscount.get("received_value").getAsDouble()) : "0.00", textFont)));
                    table.addCell(new PdfPCell(new Phrase(docDiscount.has("tcea") && !docDiscount.get("tcea").isJsonNull() ? String.format("%.6f", docDiscount.get("tcea").getAsDouble() * 100) + "%" : "0.000000%", textFont)));
                }
    
                document.add(table);
            }
        }
        else {
            // 📌 Si la cartera no tiene descuento, agregar mensaje claro
            Font noDiscountFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            document.add(new Paragraph("🔸 Esta cartera aún no ha sido descontada.\n", noDiscountFont));
        }
    }

}
