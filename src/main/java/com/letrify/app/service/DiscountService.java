package com.letrify.app.service;

import com.letrify.app.model.Bank;
import com.letrify.app.model.Discount;
import com.letrify.app.model.DiscountFee;
import com.letrify.app.model.ExchangeRate;
import com.letrify.app.model.Portfolio;
import com.letrify.app.model.Portfolio.PortfolioStatus;
import com.letrify.app.repository.BankRepository;
import com.letrify.app.repository.DiscountRepository;
import com.letrify.app.repository.ExchangeRateRepository;
import com.letrify.app.repository.PortfolioRepository;
import com.letrify.app.repository.DiscountFeeRepository;

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
    private final PortfolioRepository portfolioRepository;
    private final BankRepository bankRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final DiscountFeeRepository discountFeeRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository,
                        PortfolioRepository portfolioRepository,
                        BankRepository bankRepository,
                        ExchangeRateRepository exchangeRateRepository,
                        DiscountFeeRepository discountFeeRepository) {
        this.discountRepository = discountRepository;
        this.portfolioRepository = portfolioRepository;
        this.bankRepository = bankRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.discountFeeRepository = discountFeeRepository;
    }

    // Crear un nuevo descuento
    public Discount createDiscount(Discount discount, Long portfolioId, Long bankId, Long exchangeRateId, List<DiscountFee> fees) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio no encontrado."));
        
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado."));
        
        discount.setPortfolio(portfolio);
        discount.setBank(bank);
    
        if(exchangeRateId != null){
            ExchangeRate exchangeRate = exchangeRateRepository.findById(exchangeRateId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de cambio no encontrado."));
            discount.setExchangeRate(exchangeRate);
        }
    
        if (discountRepository.existsByPortfolio_IdAndBank_Id(portfolioId, bankId)) {
            throw new IllegalArgumentException("Ya existe un descuento registrado para esta cartera y banco.");
        }

        // 📌 Asignaciones temporales para evitar errores (luego implementarás el cálculo real):
        discount.setInterestAmount(BigDecimal.ZERO);
        discount.setTotalDiscountAmount(BigDecimal.ZERO);
        discount.setTcea(BigDecimal.ZERO);

        Discount savedDiscount = discountRepository.save(discount);

        // 🔹 Guardar fees asociados claramente:
        for (DiscountFee fee : fees) {
            fee.setDiscount(savedDiscount); // Asociar al descuento recién creado
            discountFeeRepository.save(fee);
        }
    
        // 🔹 **Actualizar el estado del portfolio** basado en la fecha del descuento:
        if (discount.getDiscountDate().isAfter(LocalDate.now())) {
            portfolio.setStatus(PortfolioStatus.PROGRAMADO);
        } else {
            portfolio.setStatus(PortfolioStatus.EN_DESCUENTO);
        }

        // Asociar el banco al portfolio
        portfolio.setBank(bank);
        portfolioRepository.save(portfolio); // Guardar cambios en el portfolio

        return savedDiscount;
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
        return discountRepository.findByPortfolio_Id(portfolioId);
    }

    // Obtener todos los descuentos de un banco específico
    public List<Discount> getDiscountsByBankId(Long bankId) {
        return discountRepository.findByBank_Id(bankId);
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
