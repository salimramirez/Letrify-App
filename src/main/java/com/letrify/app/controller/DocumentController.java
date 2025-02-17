package com.letrify.app.controller;

import com.letrify.app.model.Document;
import com.letrify.app.model.Company;
import com.letrify.app.model.Individual;
import com.letrify.app.repository.CompanyRepository;
import com.letrify.app.repository.IndividualRepository;
import com.letrify.app.service.CustomUserDetails;
import com.letrify.app.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.letrify.app.model.User; // Importa la clase User para UserType
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Importa la anotación AuthenticationPrincipal

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
    public ResponseEntity<Document> createDocument(@RequestBody Document document, 
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("📌 Valor de customer: " + document.getCustomer());

        // Verificar que el usuario autenticado tiene un ID válido
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Asignar Company si el usuario es una empresa
        if (userDetails.getUserType() == User.UserType.COMPANY && userDetails.getCompanyId() != null) {
            Company company = companyRepository.findById(userDetails.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company no encontrada con ID: " + userDetails.getCompanyId()));
            document.setCompany(company);
            System.out.println(" - 📌 Asignado a Company con ID: " + company.getId());
        }

        // Asignar Individual si el usuario es una persona
        if (userDetails.getUserType() == User.UserType.INDIVIDUAL && userDetails.getIndividualId() != null) {
            Individual individual = individualRepository.findById(userDetails.getIndividualId())
                .orElseThrow(() -> new RuntimeException("Individual no encontrado con ID: " + userDetails.getIndividualId()));
            document.setIndividual(individual);
            System.out.println(" - 📌 Asignado a Individual con ID: " + individual.getId());
        }

        // Valor por defecto para el status si no está presente
        if (document.getStatus() == null) {
            document.setStatus(Document.DocumentStatus.PENDIENTE);
        }

        System.out.println("📌 Datos del documento recibidos en el backend:");
        System.out.println(" - Cliente: " + document.getCustomer());
        System.out.println(" - Tipo de Documento: " + document.getDocumentType());
        System.out.println(" - Número: " + document.getDocumentNumber());
        System.out.println(" - Monto: " + document.getAmount());
        System.out.println(" - Moneda: " + document.getCurrency());
        System.out.println(" - Fecha de Emisión: " + document.getIssueDate());
        System.out.println(" - Fecha de Vencimiento: " + document.getDueDate());
        System.out.println(" - Estado: " + document.getStatus());
        System.out.println(" - Descripción: " + document.getDescription());
        System.out.println(" - Company ID: " + (document.getCompany() != null ? document.getCompany().getId() : "null"));
        System.out.println(" - Individual ID: " + (document.getIndividual() != null ? document.getIndividual().getId() : "null"));

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
