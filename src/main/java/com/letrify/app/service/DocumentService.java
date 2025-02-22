package com.letrify.app.service;

import com.letrify.app.model.Document;
import com.letrify.app.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PortfolioService portfolioService;

    public DocumentService(DocumentRepository documentRepository, PortfolioService portfolioService) {
        this.documentRepository = documentRepository;
        this.portfolioService = portfolioService; //
    }

    // Método para listar todos los documentos
    public List<Document> findAllDocuments() {
        return documentRepository.findAll();
    }

    // Método para buscar un documento por ID
    public Document findDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));
    }

    // Método para registrar o actualizar un documento
    public Document saveDocument(Document document) {
        validateDocumentDates(document);
        updateDiscountDays(document); // Recalcular discountDays si hay discountDate
    
        Document savedDoc = documentRepository.save(document);
    
        System.out.println("✅ Documento guardado con éxito.");

        // return documentRepository.save(document);
        return savedDoc;
    }

    // Método para eliminar un documento por ID y quitarlo de la cartera si es necesario
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se puede eliminar, el documento no existe con ID: " + id));

        // Si el documento pertenece a una cartera, eliminarlo de la cartera primero
        if (document.getPortfolio() != null) {
            portfolioService.removeDocumentFromPortfolio(id);
        }

        documentRepository.deleteById(id);
        System.out.println("✅ Documento eliminado correctamente.");
}

    // Método para validar las fechas del documento
    private void validateDocumentDates(Document document) {
        if (document.getDueDate().isBefore(document.getIssueDate())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha de emisión.");
        }
    }

    // Método para asignar discountDate y recalcular discountDays
    public Document applyDiscountToDocument(Long documentId, LocalDate discountDate) {
        Document document = findDocumentById(documentId);
        document.setDiscountDate(discountDate);
        updateDiscountDays(document);
        return documentRepository.save(document);
    }

    // Método para actualizar discountDate en todos los documentos de un portfolio
    public void applyDiscountToPortfolio(Long portfolioId, LocalDate discountDate) {
        List<Document> documents = documentRepository.findByPortfolio_Id(portfolioId);

        for (Document doc : documents) {
            doc.setDiscountDate(discountDate);
            updateDiscountDays(doc);
            documentRepository.save(doc);
        }
        System.out.println("✅ Fecha de descuento aplicada a todos los documentos del portfolio.");
    }

    // Método para calcular discountDays cuando discountDate cambia
    private void updateDiscountDays(Document document) {
        if (document.getDiscountDate() != null && document.getDueDate() != null) {
            document.setDiscountDays((int) java.time.temporal.ChronoUnit.DAYS.between(document.getDiscountDate(), document.getDueDate()));
        } else {
            document.setDiscountDays(null);
        }
    }

    // Buscar documentos por empresa
    public List<Document> findDocumentsByCompanyId(Long companyId) {
        return documentRepository.findByCompany_Id(companyId); // Método corregido
    }
    
    // Buscar documentos por individuo
    public List<Document> findDocumentsByIndividualId(Long individualId) {
        return documentRepository.findByIndividual_Id(individualId); // Método corregido
    }

    // Buscar documentos por empresa y estado
    public List<Document> findDocumentsByCompanyIdAndStatus(Long companyId, Document.DocumentStatus status) {
        return documentRepository.findByCompany_IdAndStatus(companyId, status);
    }

    // Buscar documentos por individuo y estado
    public List<Document> findDocumentsByIndividualIdAndStatus(Long individualId, Document.DocumentStatus status) {
        return documentRepository.findByIndividual_IdAndStatus(individualId, status);
    }
}