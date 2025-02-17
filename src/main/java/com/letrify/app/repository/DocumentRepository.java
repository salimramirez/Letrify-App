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

    List<Document> findByCompany_Id(Long companyId);

    List<Document> findByIndividual_Id(Long individualId);

}
