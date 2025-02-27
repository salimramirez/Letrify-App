package com.letrify.app.controller;

import com.letrify.app.model.BankFee;
import com.letrify.app.service.BankFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/bank-fees")
public class BankFeeController {

    private final BankFeeService bankFeeService;

    public BankFeeController(BankFeeService bankFeeService) {
        this.bankFeeService = bankFeeService;
    }

    // Obtener todas las tarifas bancarias
    @GetMapping
    public ResponseEntity<List<BankFee>> getAllBankFees() {
        List<BankFee> fees = bankFeeService.getAllBankFees();
        return ResponseEntity.ok(fees);
    }

    // Obtener una tarifa por ID
    @GetMapping("/{id}")
    public ResponseEntity<BankFee> getBankFeeById(@PathVariable Long id) {
        Optional<BankFee> bankFee = bankFeeService.getBankFeeById(id);
        return bankFee.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener todas las tarifas de un banco específico
    @GetMapping("/bank/{bankId}")
    public ResponseEntity<List<BankFee>> getFeesByBankId(@PathVariable Long bankId) {
        List<BankFee> fees = bankFeeService.getFeesByBankId(bankId);
        return ResponseEntity.ok(fees);
    }

    // Crear o actualizar una tarifa bancaria
    @PostMapping
    public ResponseEntity<?> createBankFee(@RequestBody Map<String, Object> requestData) {
        try {
            Long bankId = Long.valueOf(requestData.get("bankId").toString());
            String feeName = requestData.get("feeName").toString();
            String feeType = requestData.get("feeType").toString();
            BigDecimal feeAmount = new BigDecimal(requestData.get("feeAmount").toString());
            String feeTiming = requestData.get("feeTiming").toString();
    
            BankFee savedFee = bankFeeService.createBankFee(bankId, feeName, feeType, feeAmount, feeTiming);
            return ResponseEntity.ok(savedFee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la tarifa bancaria: " + e.getMessage());
        }
    }

    // Actualizar una tarifa bancaria completa
    @PutMapping("/{id}")
    public ResponseEntity<BankFee> updateBankFee(@PathVariable Long id, @RequestBody BankFee updatedBankFee) {
        if (!bankFeeService.getBankFeeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        updatedBankFee.setId(id);
        BankFee savedFee = bankFeeService.saveBankFee(updatedBankFee);
        return ResponseEntity.ok(savedFee);
    }

    // Actualizar solo un campo específico de la tarifa bancaria (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<BankFee> updateFeeAmount(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<BankFee> optionalBankFee = bankFeeService.getBankFeeById(id);
        if (optionalBankFee.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        BankFee bankFee = optionalBankFee.get();
    
        // Actualizar solo el feeAmount si está presente en el JSON
        if (updates.containsKey("feeAmount")) {
            bankFee.setFeeAmount(new BigDecimal(updates.get("feeAmount").toString()));
        }
    
        BankFee updatedFee = bankFeeService.saveBankFee(bankFee);
        return ResponseEntity.ok(updatedFee);
    }    

    // Eliminar una tarifa bancaria por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankFee(@PathVariable Long id) {
        try {
            bankFeeService.deleteBankFee(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
