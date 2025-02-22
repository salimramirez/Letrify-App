package com.letrify.app.controller;

import com.letrify.app.model.Discount;
import com.letrify.app.service.DiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // Obtener todos los descuentos
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        List<Discount> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discounts);
    }

    // Obtener un descuento por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Long id) {
        Optional<Discount> discount = discountService.getDiscountById(id);
        return discount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo descuento con validaciones
    @PostMapping
    public ResponseEntity<?> createDiscount(@RequestBody Discount discount) {
        try {
            Discount createdDiscount = discountService.createDiscount(discount);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDiscount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Actualizar un descuento existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable Long id, @RequestBody Discount discount) {
        Optional<Discount> updatedDiscount = discountService.updateDiscount(id, discount);

        if (updatedDiscount.isPresent()) {
            return ResponseEntity.ok(updatedDiscount.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Descuento no encontrado.");
        }
    }

    // Eliminar un descuento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable Long id) {
        if (discountService.deleteDiscount(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Descuento no encontrado.");
        }
    }

    // Obtener descuentos con una TCEA superior a un valor dado
    @GetMapping("/tcea-greater-than/{value}")
    public ResponseEntity<List<Discount>> getDiscountsByTceaGreaterThan(@PathVariable BigDecimal value) {
        List<Discount> discounts = discountService.getDiscountsByTceaGreaterThan(value);
        return ResponseEntity.ok(discounts);
    }

    // Obtener descuentos dentro de un rango de fechas
    @GetMapping("/date-range")
    public ResponseEntity<List<Discount>> getDiscountsByDateRange(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        List<Discount> discounts = discountService.getDiscountsByDateRange(startDate, endDate);
        return ResponseEntity.ok(discounts);
    }
}
