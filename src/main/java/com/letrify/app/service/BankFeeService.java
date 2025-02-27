package com.letrify.app.service;

import com.letrify.app.model.Bank;
import com.letrify.app.repository.BankRepository;
import com.letrify.app.model.BankFee;
import com.letrify.app.repository.BankFeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BankFeeService {

    private final BankFeeRepository bankFeeRepository;
    private final BankRepository bankRepository; // Agregamos el repositorio del banco

    public BankFeeService(BankFeeRepository bankFeeRepository, BankRepository bankRepository) {
        this.bankFeeRepository = bankFeeRepository;
        this.bankRepository = bankRepository;
    }

    // Método para crear una nueva tarifa bancaria con el banco asociado
    @Transactional
    public BankFee createBankFee(Long bankId, String feeName, String feeType, BigDecimal feeAmount, String feeTiming) {
        // Buscar el banco por ID
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado con ID: " + bankId));

        // Crear nueva tarifa bancaria
        BankFee bankFee = new BankFee();
        bankFee.setBank(bank);
        bankFee.setFeeName(feeName);
        bankFee.setFeeType(BankFee.FeeType.valueOf(feeType)); // Convertir string a Enum
        bankFee.setFeeAmount(feeAmount);
        bankFee.setFeeTiming(BankFee.FeeTiming.valueOf(feeTiming));

        // Guardar en la base de datos
        return bankFeeRepository.save(bankFee);
    }

    // Obtener todas las tarifas bancarias
    public List<BankFee> getAllBankFees() {
        return bankFeeRepository.findAll();
    }

    // Obtener una tarifa por ID
    public Optional<BankFee> getBankFeeById(Long id) {
        return bankFeeRepository.findById(id);
    }

    // Obtener todas las tarifas de un banco específico
    public List<BankFee> getFeesByBankId(Long bankId) {
        return bankFeeRepository.findByBank_Id(bankId);
    }

    // Crear o actualizar una tarifa bancaria
    @Transactional
    public BankFee saveBankFee(BankFee bankFee) {
        return bankFeeRepository.save(bankFee);
    }

    // Eliminar una tarifa bancaria por ID
    @Transactional
    public void deleteBankFee(Long id) {
        if (bankFeeRepository.existsById(id)) {
            bankFeeRepository.deleteById(id);
        } else {
            throw new RuntimeException("BankFee con ID " + id + " no encontrado.");
        }
    }
}
