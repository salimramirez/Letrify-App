package com.letrify.app.repository;

import com.letrify.app.model.DocumentDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentDiscountRepository extends JpaRepository<DocumentDiscount, Long> {

    // Obtener todos los descuentos aplicados a un documento específico
    List<DocumentDiscount> findByDocument_Id(Long documentId);

    // Obtener todos los documentos dentro de un descuento específico
    List<DocumentDiscount> findByDiscount_Id(Long discountId);
}
