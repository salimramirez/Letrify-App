package com.letrify.app.controller;

import com.letrify.app.model.DocumentDiscount;
import com.letrify.app.service.DocumentDiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/document-discounts")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen (ajustable si es necesario)
public class DocumentDiscountController {

    private final DocumentDiscountService documentDiscountService;

    public DocumentDiscountController(DocumentDiscountService documentDiscountService) {
        this.documentDiscountService = documentDiscountService;
    }

    // Obtener todos los descuentos aplicados a documentos
    @GetMapping
    public ResponseEntity<List<DocumentDiscount>> getAllDocumentDiscounts() {
        List<DocumentDiscount> discounts = documentDiscountService.getAllDocumentDiscounts();
        return ResponseEntity.ok(discounts);
    }

    // Obtener un descuento específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDocumentDiscountById(@PathVariable Long id) {
        Optional<DocumentDiscount> documentDiscount = documentDiscountService.getDocumentDiscountById(id);
        return documentDiscount.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Descuento de documento no encontrado."));
    }

    // Obtener todos los descuentos aplicados a un documento específico
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<DocumentDiscount>> getDiscountsByDocumentId(@PathVariable Long documentId) {
        List<DocumentDiscount> discounts = documentDiscountService.getDiscountsByDocumentId(documentId);
        return ResponseEntity.ok(discounts);
    }

    // Obtener todos los documentos dentro de un descuento específico
    @GetMapping("/discount/{discountId}")
    public ResponseEntity<List<DocumentDiscount>> getDocumentsByDiscountId(@PathVariable Long discountId) {
        List<DocumentDiscount> documents = documentDiscountService.getDocumentsByDiscountId(discountId);
        return ResponseEntity.ok(documents);
    }

    // Crear un nuevo registro de descuento de documento
    @PostMapping
    public ResponseEntity<DocumentDiscount> createDocumentDiscount(@RequestBody DocumentDiscount documentDiscount) {
        DocumentDiscount savedDocumentDiscount = documentDiscountService.saveDocumentDiscount(documentDiscount);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDocumentDiscount);
    }

    // Actualizar un descuento de documento existente
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDocumentDiscount(@PathVariable Long id, @RequestBody DocumentDiscount updatedDocumentDiscount) {
        Optional<DocumentDiscount> updatedDiscount = documentDiscountService.updateDocumentDiscount(id, updatedDocumentDiscount);
        return updatedDiscount.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Descuento de documento no encontrado."));
    }

    // Eliminar un descuento de documento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocumentDiscount(@PathVariable Long id) {
        boolean deleted = documentDiscountService.deleteDocumentDiscount(id);
        if (deleted) {
            return ResponseEntity.ok("Descuento de documento eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Descuento de documento no encontrado.");
        }
    }
}
