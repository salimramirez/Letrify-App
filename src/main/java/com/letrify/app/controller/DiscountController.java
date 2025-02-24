package com.letrify.app.controller;

import com.letrify.app.model.BankFee;
import com.letrify.app.model.Discount;
import com.letrify.app.model.Discount.RateType;
import com.letrify.app.model.DiscountFee;
import com.letrify.app.model.DiscountFee.FeeSource;
import com.letrify.app.model.DiscountFee.FeeType;
import com.letrify.app.model.DiscountFee.FeeTiming;
import com.letrify.app.service.DiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

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
    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<?> createDiscount(@RequestBody Map<String, Object> discountData) {
        try {
            Long portfolioId = Long.valueOf(discountData.get("portfolioId").toString());
            Long bankId = Long.valueOf(discountData.get("bankId").toString());
            Long exchangeRateId = discountData.get("exchangeRateId") != null 
                                ? Long.valueOf(discountData.get("exchangeRateId").toString())
                                : null;

            Discount discount = new Discount();
            discount.setDiscountDate(LocalDate.parse(discountData.get("discountDate").toString()));
            discount.setRateType(RateType.valueOf(discountData.get("rateType").toString()));
            discount.setRateDays(Integer.parseInt(discountData.get("rateDays").toString()));
            discount.setRate(new BigDecimal(discountData.get("rate").toString()));
            
            if(discountData.get("capitalizationDays") != null) {
                discount.setCapitalizationDays(Integer.parseInt(discountData.get("capitalizationDays").toString()));
            }

            // 🔹 Conversión de fees:
            List<Map<String, Object>> feesData = (List<Map<String, Object>>) discountData.get("fees");
            List<DiscountFee> fees = new ArrayList<>();

            for (Map<String, Object> feeData : feesData) {
                DiscountFee fee = new DiscountFee();
                fee.setFeeName(feeData.get("feeName").toString());
                fee.setFeeType(FeeType.valueOf(feeData.get("feeType").toString()));
                fee.setFeeAmount(new BigDecimal(feeData.get("feeAmount").toString()));
                fee.setFeeTiming(FeeTiming.valueOf(feeData.get("feeTiming").toString()));
                fee.setFeeSource(FeeSource.valueOf(feeData.get("feeSource").toString()));
    
                // BankFeeId opcional
                if (feeData.get("bankFeeId") != null) {
                    BankFee bankFee = new BankFee();
                    bankFee.setId(Long.valueOf(feeData.get("bankFeeId").toString()));
                    fee.setBankFee(bankFee);
                }
    
                fees.add(fee);
            }

            // ✅ Ahora llama correctamente al método del servicio:
            Discount createdDiscount = discountService.createDiscount(discount, portfolioId, bankId, exchangeRateId, fees);

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
