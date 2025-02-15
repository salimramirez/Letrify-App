package com.letrify.app.repository;

import com.letrify.app.model.PortfolioDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioDocumentRepository extends JpaRepository<PortfolioDocument, Long> {

    // Buscar todas las relaciones por el ID de la cartera
    List<PortfolioDocument> findByPortfolioId(Long portfolioId);

    // Buscar todas las relaciones por el ID del documento
    List<PortfolioDocument> findByDocumentId(Long documentId);

    // Verificar si un documento está vinculado a una cartera específica
    Optional<PortfolioDocument> findByPortfolioIdAndDocumentId(Long portfolioId, Long documentId);

    // Contar cuántos documentos están vinculados a una cartera específica
    long countByPortfolioId(Long portfolioId);

    // Eliminar todas las relaciones de una cartera específica
    void deleteByPortfolioId(Long portfolioId);
}
