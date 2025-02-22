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
import java.time.LocalDate;

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

    // Nuevo Endpoint: Asignar DiscountDate a un documento
    @PutMapping("/{id}/assign-discount-date")
    public ResponseEntity<Document> assignDiscountDateToDocument(@PathVariable Long id, @RequestParam LocalDate discountDate) {
        Document updatedDocument = documentService.applyDiscountToDocument(id, discountDate);
        return ResponseEntity.ok(updatedDocument);
    }

    // Nuevo Endpoint: Asignar DiscountDate a todos los documentos de un Portfolio
    @PutMapping("/portfolio/{portfolioId}/assign-discount-date")
    public ResponseEntity<String> assignDiscountDateToPortfolio(@PathVariable Long portfolioId, @RequestParam LocalDate discountDate) {
        documentService.applyDiscountToPortfolio(portfolioId, discountDate);
        return ResponseEntity.ok("✅ Fecha de descuento aplicada a todos los documentos del portfolio " + portfolioId);
    }

    // Obtener documentos del usuario autenticado
    @GetMapping("/user")
    public ResponseEntity<List<Document>> getDocumentsByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Document> documents;
        
        // Si el usuario es una empresa, obtener los documentos asociados a su companyId
        if (userDetails.getUserType() == User.UserType.COMPANY && userDetails.getCompanyId() != null) {
            documents = documentService.findDocumentsByCompanyId(userDetails.getCompanyId());
        }
        // Si el usuario es una persona natural, obtener los documentos asociados a su individualId
        else if (userDetails.getUserType() == User.UserType.INDIVIDUAL && userDetails.getIndividualId() != null) {
            documents = documentService.findDocumentsByIndividualId(userDetails.getIndividualId());
        }
        else {
            return ResponseEntity.ok(List.of()); // Devolver lista vacía si no tiene documentos asociados
        }

        return ResponseEntity.ok(documents);
    }

    // Obtener documentos por empresa y estado
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<Document>> getDocumentsByCompanyAndStatus(@PathVariable Long companyId, @PathVariable Document.DocumentStatus status) {
        List<Document> documents = documentService.findDocumentsByCompanyIdAndStatus(companyId, status);
        return ResponseEntity.ok(documents);
    }

    // Obtener documentos por individuo y estado
    @GetMapping("/individual/{individualId}/status/{status}")
    public ResponseEntity<List<Document>> getDocumentsByIndividualAndStatus(@PathVariable Long individualId, @PathVariable Document.DocumentStatus status) {
        List<Document> documents = documentService.findDocumentsByIndividualIdAndStatus(individualId, status);
        return ResponseEntity.ok(documents);
    }

    // Crear un nuevo documento
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document, 
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println(" Valor de customer: " + document.getCustomer());

        System.out.println("Fecha recibida en el backend - Emisión: " + document.getIssueDate());
        System.out.println("Fecha recibida en el backend - Vencimiento: " + document.getDueDate());

        // Verificar que el usuario autenticado tiene un ID válido
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Asignar Company si el usuario es una empresa
        if (userDetails.getUserType() == User.UserType.COMPANY && userDetails.getCompanyId() != null) {
            Company company = companyRepository.findById(userDetails.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company no encontrada con ID: " + userDetails.getCompanyId()));
            document.setCompany(company);
            System.out.println(" - Asignado a Company con ID: " + company.getId());
        }

        // Asignar Individual si el usuario es una persona
        if (userDetails.getUserType() == User.UserType.INDIVIDUAL && userDetails.getIndividualId() != null) {
            Individual individual = individualRepository.findById(userDetails.getIndividualId())
                .orElseThrow(() -> new RuntimeException("Individual no encontrado con ID: " + userDetails.getIndividualId()));
            document.setIndividual(individual);
            System.out.println(" - Asignado a Individual con ID: " + individual.getId());
        }

        // Valor por defecto para el status si no está presente
        if (document.getStatus() == null) {
            document.setStatus(Document.DocumentStatus.PENDIENTE);
        }

        System.out.println("Datos del documento recibidos en el backend:");
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
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        Document existingDocument = documentService.findDocumentById(id);
        
        if (existingDocument == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Documento no encontrado.");
        }
    
        // Actualizar los campos permitidos
        existingDocument.setCustomer(document.getCustomer());
        existingDocument.setDocumentType(document.getDocumentType());
        existingDocument.setDocumentNumber(document.getDocumentNumber());
        existingDocument.setAmount(document.getAmount());
        existingDocument.setCurrency(document.getCurrency());
        existingDocument.setIssueDate(document.getIssueDate());
        existingDocument.setDueDate(document.getDueDate());
        existingDocument.setDescription(document.getDescription());
    
        // Si cambia la discountDate, recalcular discountDays
        if (document.getDiscountDate() != null) {
            existingDocument.setDiscountDate(document.getDiscountDate());
        }
    
        try {
            Document updatedDocument = documentService.saveDocument(existingDocument);
            return ResponseEntity.ok(updatedDocument);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el documento.");
        }
    }

    // Eliminar un documento por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Documento no encontrado.");
        }
    }
}
