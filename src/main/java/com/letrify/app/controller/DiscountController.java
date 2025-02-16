package com.letrify.app.controller;

import com.letrify.app.model.Discount;
import com.letrify.app.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // Crear un nuevo descuento
    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        Discount createdDiscount = discountService.createDiscount(discount);
        return ResponseEntity.ok(createdDiscount);
    }

    // Actualizar un descuento existente
    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long id, @RequestBody Discount discount) {
        Optional<Discount> updatedDiscount = discountService.updateDiscount(id, discount);
        return updatedDiscount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un descuento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        if (discountService.deleteDiscount(id)) {
            return ResponseEntity.noContent().build();  // Eliminación exitosa
        } else {
            return ResponseEntity.notFound().build();   // No se encontró el descuento
        }
    }

    // Obtener descuentos con una TCEA superior a un valor dado
    @GetMapping("/tcea-greater-than/{value}")
    public ResponseEntity<List<Discount>> getDiscountsByTceaGreaterThan(@PathVariable BigDecimal value) {
        List<Discount> discounts = discountService.getDiscountsByTceaGreaterThan(value);
        return ResponseEntity.ok(discounts);
    }
}
