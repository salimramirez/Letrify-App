package com.letrify.app.service;

import com.letrify.app.model.Document;
import com.letrify.app.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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

        System.out.println("🟢 Fecha ANTES de guardar en Hibernate:");
        System.out.println("   - Fecha de Emisión: " + document.getIssueDate());
        System.out.println("   - Fecha de Vencimiento: " + document.getDueDate());
    
        Document savedDoc = documentRepository.save(document);
    
        System.out.println("✅ Fecha DESPUÉS de guardar en Hibernate:");
        System.out.println("   - Fecha de Emisión: " + savedDoc.getIssueDate());
        System.out.println("   - Fecha de Vencimiento: " + savedDoc.getDueDate());

        return documentRepository.save(document);
    }

    // Método para eliminar un documento por ID
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("No se puede eliminar, el documento no existe con ID: " + id);
        }
        documentRepository.deleteById(id);
    }

    // Método para validar las fechas del documento
    private void validateDocumentDates(Document document) {
        if (document.getDueDate().isBefore(document.getIssueDate())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha de emisión.");
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