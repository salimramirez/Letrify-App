package com.letrify.app.controller;

import com.letrify.app.model.Document;
import com.letrify.app.model.Document.DocumentStatus;
import com.letrify.app.model.Company;
import com.letrify.app.model.Individual;
import com.letrify.app.repository.CompanyRepository;
import com.letrify.app.repository.IndividualRepository;
import com.letrify.app.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final CompanyRepository companyRepository;
    private final IndividualRepository individualRepository;

    public DocumentController(DocumentService documentService, 
                              CompanyRepository companyRepository, 
                              IndividualRepository individualRepository) {
        this.documentService = documentService;
        this.companyRepository = companyRepository;
        this.individualRepository = individualRepository;
    }

    // Obtener todos los documentos
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.findAllDocuments();
        return ResponseEntity.ok(documents);
    }

    // Obtener un documento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Document document = documentService.findDocumentById(id);
        return ResponseEntity.ok(document);
    }

    // Crear un nuevo documento
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        System.out.println("Valor de customer: " + document.getCustomer());

        // Asignar Company si está presente
        if (document.getCompany() != null && document.getCompany().getId() != null) {
            Company company = companyRepository.findById(document.getCompany().getId())
                    .orElseThrow(() -> new RuntimeException("Company no encontrada"));
            document.setCompany(company);
        }

        // Asignar Individual si está presente
        if (document.getIndividual() != null && document.getIndividual().getId() != null) {
            Individual individual = individualRepository.findById(document.getIndividual().getId())
                    .orElseThrow(() -> new RuntimeException("Individual no encontrado"));
            document.setIndividual(individual);
        }

        // Valor por defecto para el status si no está presente
        if (document.getStatus() == null) {
            document.setStatus(DocumentStatus.PENDIENTE);
        }

        Document savedDocument = documentService.saveDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDocument);
    }

    // Actualizar un documento existente
    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        document.setId(id);
        Document updatedDocument = documentService.saveDocument(document);
        return ResponseEntity.ok(updatedDocument);
    }

    // Eliminar un documento por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
