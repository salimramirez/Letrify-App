package com.letrify.app.controller;

import com.letrify.app.model.Document;
import com.letrify.app.model.PortfolioDocument;
import com.letrify.app.service.PortfolioDocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/documents")
public class PortfolioDocumentController {

    private final PortfolioDocumentService portfolioDocumentService;

    public PortfolioDocumentController(PortfolioDocumentService portfolioDocumentService) {
        this.portfolioDocumentService = portfolioDocumentService;
    }

    // Asociar un documento a una cartera
    @PostMapping("/{documentId}")
    public ResponseEntity<PortfolioDocument> addDocumentToPortfolio(
            @PathVariable Long portfolioId,
            @PathVariable Long documentId) {
        
        PortfolioDocument portfolioDocument = portfolioDocumentService.addDocumentToPortfolioByIds(portfolioId, documentId);
        return ResponseEntity.ok(portfolioDocument);
    }

    // Listar todos los documentos asociados a una cartera
    @GetMapping
    public ResponseEntity<List<Document>> getDocumentsByPortfolioId(@PathVariable Long portfolioId) {
        List<Document> documents = portfolioDocumentService.getDocumentsByPortfolio(portfolioId);
        return ResponseEntity.ok(documents);
    }

    // Eliminar la asociación entre un documento y una cartera
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> removeDocumentFromPortfolio(
            @PathVariable Long portfolioId,
            @PathVariable Long documentId) {
        
        portfolioDocumentService.removeDocumentFromPortfolio(portfolioId, documentId);
        return ResponseEntity.noContent().build();
    }
}
