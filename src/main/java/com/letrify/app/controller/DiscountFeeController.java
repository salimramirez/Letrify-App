package com.letrify.app.controller;

import com.letrify.app.model.DiscountFee;
import com.letrify.app.service.DiscountFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/discounts/{discountId}/fees")
public class DiscountFeeController {

    private final DiscountFeeService discountFeeService;

    public DiscountFeeController(DiscountFeeService discountFeeService) {
        this.discountFeeService = discountFeeService;
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
        return ResponseEntity.ok(fee);
    }

    // Obtener los gastos de un descuento según feeTiming (INICIO o FINAL)
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
        // Validar que el campo feeTiming no sea nulo
        if (discountFee.getFeeTiming() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        discountFee.getDiscount().setId(discountId); // Asociar el gasto al descuento correspondiente
        DiscountFee createdFee = discountFeeService.createDiscountFee(discountFee);
        return ResponseEntity.ok(createdFee);
    }

    // Actualizar un gasto existente
    @PutMapping("/{id}")
    public ResponseEntity<DiscountFee> updateDiscountFee(@PathVariable Long discountId, @PathVariable Long id, @RequestBody DiscountFee discountFee) {
        DiscountFee updatedFee = discountFeeService.updateDiscountFee(id, discountFee);
        return ResponseEntity.ok(updatedFee);
    }

    // Eliminar un gasto específico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscountFee(@PathVariable Long discountId, @PathVariable Long id) {
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
