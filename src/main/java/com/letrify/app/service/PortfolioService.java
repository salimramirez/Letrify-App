package com.letrify.app.service;

import com.letrify.app.model.Portfolio;
import com.letrify.app.model.Discount;
import com.letrify.app.model.Document;
import com.letrify.app.model.DocumentDiscount;
import com.letrify.app.repository.PortfolioRepository;
import com.letrify.app.repository.DocumentRepository;
import com.letrify.app.repository.DiscountRepository;
import com.letrify.app.repository.DocumentDiscountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final DocumentRepository documentRepository; // Agregamos el repositorio de documentos
    private final DiscountRepository discountRepository; // Agregar DiscountRepository
    private final DocumentDiscountRepository documentDiscountRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, 
                            DocumentRepository documentRepository, 
                            DiscountRepository discountRepository,
                            DocumentDiscountRepository documentDiscountRepository) { // Inyectar DiscountRepository
        this.portfolioRepository = portfolioRepository;
        this.documentRepository = documentRepository;
        this.discountRepository = discountRepository;
        this.documentDiscountRepository = documentDiscountRepository;
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

    public List<Map<String, Object>> getAllPortfoliosWithDiscounts() {
        List<Portfolio> portfolios = portfolioRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Portfolio portfolio : portfolios) {
            Map<String, Object> portfolioData = new HashMap<>();
            portfolioData.put("id", portfolio.getId());
            portfolioData.put("portfolioName", portfolio.getPortfolioName());
            portfolioData.put("description", portfolio.getDescription());
            portfolioData.put("currency", portfolio.getCurrency());
            // portfolioData.put("discountDate", portfolio.getDiscountDate());
            portfolioData.put("status", portfolio.getStatus());
            portfolioData.put("documentCount", portfolio.getDocumentCount());

            // Incluir documentos asociados a la cartera
            List<Document> documents = documentRepository.findByPortfolio_Id(portfolio.getId());
            portfolioData.put("documents", documents);

            // 📌 Buscar el descuento correspondiente a la cartera
            Discount discount = discountRepository.findTopByPortfolio_IdOrderByDiscountDateDesc(portfolio.getId());
            if (discount != null) {
                Map<String, Object> discountData = new HashMap<>();
                discountData.put("bank", discount.getBank().getBankName());
                discountData.put("discount_date", discount.getDiscountDate().toString());
                discountData.put("tcea", discount.getTcea());
                discountData.put("interest_amount", discount.getInterestAmount());
                discountData.put("total_cost", discount.getTotalDiscountAmount());
                portfolioData.put("discount", discountData);

                // 📌 Incluir los descuentos individuales de documentos
                List<DocumentDiscount> documentDiscounts = documentDiscountRepository.findByDiscount_Id(discount.getId());
                List<Map<String, Object>> documentDiscountsList = new ArrayList<>();

                for (DocumentDiscount docDiscount : documentDiscounts) {
                    Map<String, Object> docDiscountData = new HashMap<>();
                    docDiscountData.put("document_number", docDiscount.getDocument().getDocumentNumber());
                    docDiscountData.put("nominal_value", docDiscount.getNominalValue());
                    docDiscountData.put("discount_rate", docDiscount.getDiscountRate());
                    docDiscountData.put("interest_amount", docDiscount.getInterestAmount());
                    docDiscountData.put("net_value", docDiscount.getNetValue());
                    docDiscountData.put("received_value", docDiscount.getReceivedValue());
                    docDiscountData.put("delivered_value", docDiscount.getDeliveredValue());
                    docDiscountData.put("tcea", docDiscount.getTcea());
                    documentDiscountsList.add(docDiscountData);
                }

                discountData.put("document_discounts", documentDiscountsList);
                portfolioData.put("discount", discountData);

            }

            result.add(portfolioData);
        }

        return result;
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
