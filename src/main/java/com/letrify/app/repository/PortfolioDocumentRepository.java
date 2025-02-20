package com.letrify.app.repository;

import com.letrify.app.model.PortfolioDocument;
import com.letrify.app.model.PortfolioDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioDocumentRepository extends JpaRepository<PortfolioDocument, PortfolioDocumentId> {

    // Buscar documentos dentro de una cartera específica
    List<PortfolioDocument> findByPortfolioId(Long portfolioId);

    // Buscar carteras en las que se encuentra un documento específico
    List<PortfolioDocument> findByDocumentId(Long documentId);

    // Eliminar todos los documentos de una cartera
    void deleteByPortfolioId(Long portfolioId);

    // Eliminar todas las asociaciones de un documento
    void deleteByDocumentId(Long documentId);

    // Verificar si un documento está vinculado a una cartera específica
    Optional<PortfolioDocument> findByPortfolioIdAndDocumentId(Long portfolioId, Long documentId);

    // Contar cuántos documentos están vinculados a una cartera específica
    long countByPortfolioId(Long portfolioId);
}
