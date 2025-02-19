package com.letrify.app.service;

import com.letrify.app.exception.ResourceNotFoundException;
import com.letrify.app.model.DiscountFee;
import com.letrify.app.repository.DiscountFeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DiscountFeeService {

    private final DiscountFeeRepository discountFeeRepository;

    public DiscountFeeService(DiscountFeeRepository discountFeeRepository) {
        this.discountFeeRepository = discountFeeRepository;
    }

    // Crear un nuevo gasto asociado a un descuento
    public DiscountFee createDiscountFee(DiscountFee discountFee) {
        if (discountFeeRepository.existsByDiscountIdAndFeeType(discountFee.getDiscount().getId(), discountFee.getFeeType())) {
            throw new IllegalArgumentException("Ya existe un gasto de tipo '" + discountFee.getFeeType() + "' para este descuento.");
        }

        if (discountFee.getFeeTiming() == null) {
            throw new IllegalArgumentException("El campo feeTiming es obligatorio.");
        }

        return discountFeeRepository.save(discountFee);
    }

    // Actualizar un gasto existente
    public DiscountFee updateDiscountFee(Long id, DiscountFee updatedFee) {
        DiscountFee discountFee = discountFeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con ID: " + id));

        discountFee.setFeeType(updatedFee.getFeeType());
        discountFee.setAmount(updatedFee.getAmount());
        discountFee.setFeeTiming(updatedFee.getFeeTiming());

        return discountFeeRepository.save(discountFee);
    }

    // Eliminar un gasto por su ID
    public void deleteDiscountFee(Long id) {
        DiscountFee discountFee = discountFeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con ID: " + id));
        discountFeeRepository.delete(discountFee);
    }

    // Obtener todos los gastos
    public List<DiscountFee> getAllDiscountFees() {
        return discountFeeRepository.findAll();
    }

    // Obtener un gasto por su ID
    public DiscountFee getDiscountFeeById(Long id) {
        return discountFeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con ID: " + id));
    }

    // Obtener todos los gastos asociados a un descuento
    public List<DiscountFee> getFeesByDiscountId(Long discountId) {
        return discountFeeRepository.findByDiscountId(discountId);
    }

    // Obtener todos los gastos según su feeTiming (INICIO o FINAL)
    public List<DiscountFee> getFeesByFeeTiming(DiscountFee.FeeTiming feeTiming) {
        return discountFeeRepository.findByFeeTiming(feeTiming);
    }

    // Obtener todos los gastos de un descuento según feeTiming (INICIO o FINAL)
    public List<DiscountFee> getFeesByDiscountAndFeeTiming(Long discountId, DiscountFee.FeeTiming feeTiming) {
        return discountFeeRepository.findByDiscountIdAndFeeTiming(discountId, feeTiming);
    }

    // Calcular el total de gastos asociados a un descuento
    public BigDecimal calculateTotalFeesForDiscount(Long discountId) {
        List<DiscountFee> fees = discountFeeRepository.findByDiscountId(discountId);
        return fees.stream()
                .map(DiscountFee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
