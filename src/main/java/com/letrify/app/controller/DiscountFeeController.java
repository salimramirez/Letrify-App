package com.letrify.app.controller;

import com.letrify.app.model.Discount;
import com.letrify.app.model.DiscountFee;
import com.letrify.app.service.DiscountService;
import com.letrify.app.service.DiscountFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discounts/{discountId}/fees")
public class DiscountFeeController {

    private final DiscountFeeService discountFeeService;
    private final DiscountService discountService;

    public DiscountFeeController(DiscountFeeService discountFeeService, DiscountService discountService) {
        this.discountFeeService = discountFeeService;
        this.discountService = discountService;
    }

    // Obtener todos los gastos asociados a un descuento
    @GetMapping
    public ResponseEntity<List<DiscountFee>> getFeesByDiscountId(@PathVariable Long discountId) {
        List<DiscountFee> fees = discountFeeService.getFeesByDiscountId(discountId);
        return ResponseEntity.ok(fees);
    }

    // Obtener un gasto específico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<DiscountFee> getDiscountFeeById(@PathVariable Long discountId, @PathVariable Long id) {
        DiscountFee fee = discountFeeService.getDiscountFeeById(id);

        // Validar que el fee pertenece al descuento correcto
        if (!fee.getDiscount().getId().equals(discountId)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(fee);
    }

    // Obtener los gastos de un descuento según feeTiming (INICIAL o FINAL)
    @GetMapping("/timing/{feeTiming}")
    public ResponseEntity<List<DiscountFee>> getFeesByDiscountAndTiming(
            @PathVariable Long discountId,
            @PathVariable DiscountFee.FeeTiming feeTiming) {
        List<DiscountFee> fees = discountFeeService.getFeesByDiscountAndFeeTiming(discountId, feeTiming);
        return ResponseEntity.ok(fees);
    }

    // Crear un nuevo gasto para un descuento
    @PostMapping
    public ResponseEntity<DiscountFee> createDiscountFee(@PathVariable Long discountId, @RequestBody DiscountFee discountFee) {
        // Validar que el descuento existe
        Optional<Discount> discountOptional = discountService.getDiscountById(discountId);
        if (discountOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Validar que el campo feeTiming no sea nulo
        if (discountFee.getFeeTiming() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Asociar el gasto al descuento correspondiente
        discountFee.setDiscount(discountOptional.get());

        // Validar que no exista ya un fee con el mismo tipo para este descuento
        if (discountFeeService.existsByDiscount_IdAndFeeType(discountId, discountFee.getFeeType())) {
            return ResponseEntity.badRequest().body(null);
        }

        DiscountFee createdFee = discountFeeService.createDiscountFee(discountFee);
        return ResponseEntity.ok(createdFee);
    }

    // Actualizar un gasto existente
    @PutMapping("/{id}")
    public ResponseEntity<DiscountFee> updateDiscountFee(@PathVariable Long discountId, @PathVariable Long id, @RequestBody DiscountFee discountFee) {
        // Validar que el fee pertenece al descuento correcto
        DiscountFee existingFee = discountFeeService.getDiscountFeeById(id);
        if (!existingFee.getDiscount().getId().equals(discountId)) {
            return ResponseEntity.badRequest().build();
        }

        DiscountFee updatedFee = discountFeeService.updateDiscountFee(id, discountFee);
        return ResponseEntity.ok(updatedFee);
    }

    // Eliminar un gasto específico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscountFee(@PathVariable Long discountId, @PathVariable Long id) {
        DiscountFee fee = discountFeeService.getDiscountFeeById(id);

        // Validar que el fee pertenece al descuento correcto
        if (!fee.getDiscount().getId().equals(discountId)) {
            return ResponseEntity.badRequest().build();
        }

        discountFeeService.deleteDiscountFee(id);
        return ResponseEntity.noContent().build();
    }

    // Calcular el total de gastos asociados a un descuento
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> calculateTotalFeesForDiscount(@PathVariable Long discountId) {
        BigDecimal totalFees = discountFeeService.calculateTotalFeesForDiscount(discountId);
        return ResponseEntity.ok(totalFees);
    }
}
