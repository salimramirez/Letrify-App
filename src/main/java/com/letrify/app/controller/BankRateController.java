package com.letrify.app.controller;

import com.letrify.app.model.BankRate;
import com.letrify.app.service.BankRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bank-rates")
public class BankRateController {

    private final BankRateService bankRateService;

    public BankRateController(BankRateService bankRateService) {
        this.bankRateService = bankRateService;
    }

    // Obtener todas las tasas
    @GetMapping
    public ResponseEntity<List<BankRate>> getAllBankRates() {
        List<BankRate> bankRates = bankRateService.getAllBankRates();
        return ResponseEntity.ok(bankRates);
    }

    // Obtener una tasa por ID
    @GetMapping("/{id}")
    public ResponseEntity<BankRate> getBankRateById(@PathVariable Long id) {
        Optional<BankRate> bankRate = bankRateService.getBankRateById(id);
        return bankRate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear una nueva tasa
    @PostMapping
    public ResponseEntity<BankRate> createBankRate(@RequestBody BankRate bankRate) {
        BankRate createdBankRate = bankRateService.createBankRate(bankRate);
        return ResponseEntity.ok(createdBankRate);
    }

    // Actualizar una tasa existente
    @PutMapping("/{id}")
    public ResponseEntity<BankRate> updateBankRate(@PathVariable Long id, @RequestBody BankRate bankRate) {
        Optional<BankRate> updatedBankRate = bankRateService.updateBankRate(id, bankRate);
        return updatedBankRate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una tasa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankRate(@PathVariable Long id) {
        if (bankRateService.deleteBankRate(id)) {
            return ResponseEntity.noContent().build();  // Eliminación exitosa
        } else {
            return ResponseEntity.notFound().build();   // No se encontró la tasa
        }
    }
}
