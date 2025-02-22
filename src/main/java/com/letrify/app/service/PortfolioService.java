package com.letrify.app.service;

import com.letrify.app.model.Portfolio;
import com.letrify.app.model.Document;
import com.letrify.app.repository.PortfolioRepository;
import com.letrify.app.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final DocumentRepository documentRepository; // Agregamos el repositorio de documentos

    public PortfolioService(PortfolioRepository portfolioRepository, DocumentRepository documentRepository) {
        this.portfolioRepository = portfolioRepository;
        this.documentRepository = documentRepository; 
    }

    // Crear una nueva cartera
    public Portfolio createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    // Actualizar una cartera existente
    public Optional<Portfolio> updatePortfolio(Long id, Portfolio updatedPortfolio) {
        return portfolioRepository.findById(id).map(portfolio -> {
            portfolio.setPortfolioName(updatedPortfolio.getPortfolioName());
            portfolio.setDescription(updatedPortfolio.getDescription());
            portfolio.setDiscountDate(updatedPortfolio.getDiscountDate());
            portfolio.setBank(updatedPortfolio.getBank());
            portfolio.setStatus(updatedPortfolio.getStatus());
            return portfolioRepository.save(portfolio);
        });
    }

    // Eliminar una cartera solo si existe
    public boolean deletePortfolio(Long id) {
        if (portfolioRepository.existsById(id)) { // Verifica si existe la cartera
            portfolioRepository.deleteById(id);
            return true;  // Eliminación exitosa
        }
        return false;  // La cartera no existe
    }

    // Obtener todas las carteras
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    // Obtener una cartera por su ID
    public Optional<Portfolio> getPortfolioById(Long id) {
        return portfolioRepository.findById(id);
    }

    // Calcular la TCEA para una cartera (futuro)
    public BigDecimal calculateTCEA(Portfolio portfolio) {
        // Placeholder para el cálculo real del TCEA
        return BigDecimal.ZERO;
    }

    // Nuevo método: Agregar un documento a una cartera
    public boolean addDocumentToPortfolio(Long portfolioId, Long documentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Cartera no encontrada"));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        // Verificar si el documento ya pertenece a otra cartera
        if (document.getPortfolio() != null) {
            throw new IllegalArgumentException("Este documento ya está asignado a otra cartera.");
        }

        // Asignar la cartera al documento y guardar
        document.setPortfolio(portfolio);
        documentRepository.save(document);

        // Actualizar documentCount en Portfolio
        int documentCount = documentRepository.countByPortfolioId(portfolioId);
        portfolioRepository.updateDocumentCount(portfolioId, documentCount);

        return true;
    }

    public boolean removeDocumentFromPortfolio(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
    
        Portfolio portfolio = document.getPortfolio();
        if (portfolio == null) {
            throw new IllegalStateException("El documento no pertenece a ninguna cartera.");
        }
    
        // Quitar la cartera del documento y guardar
        document.setPortfolio(null);
        documentRepository.save(document);
    
        // Actualizar documentCount en Portfolio
        int documentCount = documentRepository.countByPortfolioId(portfolio.getId());
        portfolioRepository.updateDocumentCount(portfolio.getId(), documentCount);
    
        return true;
    }

    // Nuevo método: Obtener documentos dentro de una cartera
    public List<Document> getDocumentsByPortfolio(Long portfolioId) {
        return documentRepository.findByPortfolio_Id(portfolioId);
    }

    // Obtener documentos de otras carteras
    public List<Document> getDocumentsFromOtherPortfolios(Long portfolioId) {
        return documentRepository.findByPortfolioIsNotNullAndPortfolio_IdNot(portfolioId);
    }    

}
