package com.letrify.app.controller;

import com.letrify.app.model.Document;
import com.letrify.app.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
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
