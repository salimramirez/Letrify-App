package com.letrify.app.controller;

import com.letrify.app.model.Document;
import com.letrify.app.model.Document.DocumentStatus;
import com.letrify.app.model.User;
import com.letrify.app.repository.UserRepository;
import com.letrify.app.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    private final UserRepository userRepository;

    public DocumentController(DocumentService documentService, UserRepository userRepository) {
        this.documentService = documentService;
        this.userRepository = userRepository;
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
        
        // Buscar y asignar Company (usuario tipo empresa)
        if (document.getCompanyId() != null) {
            User company = userRepository.findById(document.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company no encontrada"));
            document.setCompany(company);
        }

        // Buscar y asignar Individual (usuario tipo persona)
        if (document.getIndividualId() != null) {
            User individual = userRepository.findById(document.getIndividualId())
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
