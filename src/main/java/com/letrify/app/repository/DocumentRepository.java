package com.letrify.app.repository;

import com.letrify.app.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Buscar documentos por tipo
    List<Document> findByDocumentType(Document.DocumentType documentType);

    // Buscar documentos por estado
    List<Document> findByStatus(Document.DocumentStatus status);

    // Buscar documentos por cliente (customer)
    List<Document> findByCustomerContainingIgnoreCase(String customer);

    // Buscar documentos por empresa (company)
    List<Document> findByCompany_Id(Long companyId);

    // Buscar documentos por individuo (individual)
    List<Document> findByIndividual_Id(Long individualId);

    // Buscar documentos por estado
    List<Document> findByStatusIn(List<Document.DocumentStatus> statuses);

    // Buscar documentos por empresa y estado
    List<Document> findByCompany_IdAndStatus(Long companyId, Document.DocumentStatus status);

    // Buscar documentos por individuo y estado
    List<Document> findByIndividual_IdAndStatus(Long individualId, Document.DocumentStatus status);

}
