package com.letrify.app.service;

import com.letrify.app.model.Discount;
import com.letrify.app.repository.DiscountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    // Crear un nuevo descuento
    public Discount createDiscount(Discount discount) {
        if (discountRepository.existsByPortfolioIdAndBankId(discount.getPortfolio().getId(), discount.getBank().getId())) {
            throw new IllegalArgumentException("Ya existe un descuento registrado para esta cartera y banco.");
        }
        return discountRepository.save(discount);
    }

    // Actualizar un descuento existente
    public Optional<Discount> updateDiscount(Long id, Discount updatedDiscount) {
        return discountRepository.findById(id).map(discount -> {
            discount.setInterestAmount(updatedDiscount.getInterestAmount());
            discount.setTcea(updatedDiscount.getTcea());
            discount.setTotalDiscountAmount(updatedDiscount.getTotalDiscountAmount());
            return discountRepository.save(discount);
        });
    }

    // Eliminar un descuento solo si existe
    public boolean deleteDiscount(Long id) {
        if (discountRepository.existsById(id)) {
            discountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener todos los descuentos
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    // Obtener un descuento por su ID
    public Optional<Discount> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    // Obtener todos los descuentos de una cartera específica
    public List<Discount> getDiscountsByPortfolioId(Long portfolioId) {
        return discountRepository.findByPortfolioId(portfolioId);
    }

    // Obtener todos los descuentos de un banco específico
    public List<Discount> getDiscountsByBankId(Long bankId) {
        return discountRepository.findByBankId(bankId);
    }

    // Obtener descuentos con una TCEA superior a un valor dado
    public List<Discount> getDiscountsByTceaGreaterThan(BigDecimal tcea) {
        return discountRepository.findByTceaGreaterThan(tcea);
    }
}
