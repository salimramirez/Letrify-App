package com.letrify.app.service;

import com.letrify.app.model.DocumentDiscount;
import com.letrify.app.repository.DocumentDiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentDiscountService {

    private final DocumentDiscountRepository documentDiscountRepository;

    public DocumentDiscountService(DocumentDiscountRepository documentDiscountRepository) {
        this.documentDiscountRepository = documentDiscountRepository;
    }

    // Obtener todos los registros
    public List<DocumentDiscount> getAllDocumentDiscounts() {
        return documentDiscountRepository.findAll();
    }

    // Obtener un registro por ID
    public Optional<DocumentDiscount> getDocumentDiscountById(Long id) {
        return documentDiscountRepository.findById(id);
    }

    // Obtener todos los descuentos aplicados a un documento específico
    public List<DocumentDiscount> getDiscountsByDocumentId(Long documentId) {
        return documentDiscountRepository.findByDocument_Id(documentId);
    }

    // Obtener todos los documentos dentro de un descuento específico
    public List<DocumentDiscount> getDocumentsByDiscountId(Long discountId) {
        return documentDiscountRepository.findByDiscount_Id(discountId);
    }

    // Guardar un nuevo registro
    public DocumentDiscount saveDocumentDiscount(DocumentDiscount documentDiscount) {
        return documentDiscountRepository.save(documentDiscount);
    }

    // Actualizar un registro existente
    public Optional<DocumentDiscount> updateDocumentDiscount(Long id, DocumentDiscount updatedDocumentDiscount) {
        return documentDiscountRepository.findById(id).map(existingDiscount -> {
            existingDiscount.setNominalValue(updatedDocumentDiscount.getNominalValue());
            existingDiscount.setDiscountRate(updatedDocumentDiscount.getDiscountRate());
            existingDiscount.setInterestAmount(updatedDocumentDiscount.getInterestAmount());
            existingDiscount.setNetValue(updatedDocumentDiscount.getNetValue());
            existingDiscount.setReceivedValue(updatedDocumentDiscount.getReceivedValue());
            existingDiscount.setDeliveredValue(updatedDocumentDiscount.getDeliveredValue());
            existingDiscount.setTcea(updatedDocumentDiscount.getTcea());
            return documentDiscountRepository.save(existingDiscount);
        });
    }

    // Eliminar un registro por ID
    public boolean deleteDocumentDiscount(Long id) {
        if (documentDiscountRepository.existsById(id)) {
            documentDiscountRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
