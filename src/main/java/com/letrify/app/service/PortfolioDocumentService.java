package com.letrify.app.service;

import com.letrify.app.exception.ResourceNotFoundException;
import com.letrify.app.model.Document;
import com.letrify.app.model.Portfolio;
import com.letrify.app.model.PortfolioDocument;
import com.letrify.app.repository.DocumentRepository;
import com.letrify.app.repository.PortfolioDocumentRepository;
import com.letrify.app.repository.PortfolioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PortfolioDocumentService {

    private final PortfolioDocumentRepository portfolioDocumentRepository;
    private final PortfolioRepository portfolioRepository;
    private final DocumentRepository documentRepository;

    public PortfolioDocumentService(
            PortfolioDocumentRepository portfolioDocumentRepository,
            PortfolioRepository portfolioRepository,
            DocumentRepository documentRepository) {
        this.portfolioDocumentRepository = portfolioDocumentRepository;
        this.portfolioRepository = portfolioRepository;
        this.documentRepository = documentRepository;
    }

    // Asociar un documento a una cartera
    @Transactional
    public PortfolioDocument addDocumentToPortfolio(Portfolio portfolio, Document document) {
        PortfolioDocument portfolioDocument = new PortfolioDocument();
        portfolioDocument.setPortfolio(portfolio);
        portfolioDocument.setDocument(document);
        return portfolioDocumentRepository.save(portfolioDocument);
    }

    // Asociar un documento a una cartera usando sus IDs
    @Transactional
    public PortfolioDocument addDocumentToPortfolioByIds(Long portfolioId, Long documentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartera no encontrada: " + portfolioId));
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado: " + documentId));

        PortfolioDocument portfolioDocument = new PortfolioDocument();
        portfolioDocument.setPortfolio(portfolio);
        portfolioDocument.setDocument(document);

        return portfolioDocumentRepository.save(portfolioDocument);
    }

    // Obtener todos los documentos asociados a una cartera
    // public List<PortfolioDocument> getDocumentsByPortfolioId(Long portfolioId) {
    //     return portfolioDocumentRepository.findByPortfolioId(portfolioId);
    // }

    // Obtener todos los documentos asociados a una cartera
    public List<Document> getDocumentsByPortfolio(Long portfolioId) {
        List<PortfolioDocument> portfolioDocuments = portfolioDocumentRepository.findByPortfolioId(portfolioId);
        return portfolioDocuments.stream()
                .map(PortfolioDocument::getDocument)
                .collect(Collectors.toList());
    }

    // Verificar si un documento está asociado a una cartera específica
    public boolean isDocumentInPortfolio(Long portfolioId, Long documentId) {
        return portfolioDocumentRepository.findByPortfolioIdAndDocumentId(portfolioId, documentId).isPresent();
    }

    // Contar cuántos documentos están asociados a una cartera
    public long countDocumentsInPortfolio(Long portfolioId) {
        return portfolioDocumentRepository.countByPortfolioId(portfolioId);
    }

    // Eliminar todos los documentos asociados a una cartera
    @Transactional
    public void removeDocumentsFromPortfolio(Long portfolioId) {
        portfolioDocumentRepository.deleteByPortfolioId(portfolioId);
    }

    // Eliminar la asociación entre un documento y una cartera
    @Transactional
    public void removeDocumentFromPortfolio(Long portfolioId, Long documentId) {
        PortfolioDocument portfolioDocument = portfolioDocumentRepository.findByPortfolioIdAndDocumentId(portfolioId, documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada para cartera " + portfolioId + " y documento " + documentId));
        portfolioDocumentRepository.delete(portfolioDocument);
    }
}
