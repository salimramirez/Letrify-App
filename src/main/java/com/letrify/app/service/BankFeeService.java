package com.letrify.app.service;

import com.letrify.app.model.BankFee;
import com.letrify.app.repository.BankFeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BankFeeService {

    private final BankFeeRepository bankFeeRepository;

    public BankFeeService(BankFeeRepository bankFeeRepository) {
        this.bankFeeRepository = bankFeeRepository;
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
