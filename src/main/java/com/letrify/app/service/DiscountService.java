package com.letrify.app.service;

import com.letrify.app.model.Discount;
import com.letrify.app.model.ExchangeRate;
import com.letrify.app.repository.DiscountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, ExchangeRateService exchangeRateService) {
        this.discountRepository = discountRepository;
        this.exchangeRateService = exchangeRateService;
    }

    // Crear un nuevo descuento
    public Discount createDiscount(Discount discount) {
        if (discountRepository.existsByPortfolioIdAndBankId(discount.getPortfolio().getId(), discount.getBank().getId())) {
            throw new IllegalArgumentException("Ya existe un descuento registrado para esta cartera y banco.");
        }

        // Validación de fechas y tasas
        if (discount.getDiscountDate() == null) {
            throw new IllegalArgumentException("La fecha de descuento no puede ser nula.");
        }
        if (discount.getRate() == null || discount.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La tasa debe ser mayor que 0.");
        }
        if (discount.getRateDays() == null || discount.getRateDays() <= 0) {
            throw new IllegalArgumentException("El período de la tasa en días debe ser mayor a 0.");
        }

        // Obtener el tipo de cambio más reciente
        ExchangeRate latestRate = exchangeRateService.fetchAndSaveExchangeRate();
        discount.setExchangeRate(latestRate);

        return discountRepository.save(discount);
    }

    // Actualizar un descuento existente
    public Optional<Discount> updateDiscount(Long id, Discount updatedDiscount) {
        return discountRepository.findById(id).map(discount -> {
            discount.setInterestAmount(updatedDiscount.getInterestAmount());
            discount.setTcea(updatedDiscount.getTcea());
            discount.setTotalDiscountAmount(updatedDiscount.getTotalDiscountAmount());
            discount.setDiscountDate(updatedDiscount.getDiscountDate());
            discount.setRate(updatedDiscount.getRate());
            discount.setRateDays(updatedDiscount.getRateDays());
            discount.setCapitalizationDays(updatedDiscount.getCapitalizationDays());
    
            // No permitir cambiar el tipo de cambio después de la creación
            // discount.setExchangeRate(updatedDiscount.getExchangeRate()); <-- Eliminado
    
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

    // Obtener descuentos dentro de un rango de fechas
    public List<Discount> getDiscountsByDateRange(LocalDate startDate, LocalDate endDate) {
        return discountRepository.findByDiscountDateBetween(startDate, endDate);
    }
}
