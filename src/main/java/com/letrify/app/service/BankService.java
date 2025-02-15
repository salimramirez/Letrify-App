package com.letrify.app.service;

import com.letrify.app.model.Bank;
import com.letrify.app.repository.BankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    // Crear un nuevo banco
    public Bank createBank(Bank bank) {
        if (bankRepository.existsByBankName(bank.getBankName())) {
            throw new IllegalArgumentException("El banco con el nombre '" + bank.getBankName() + "' ya existe.");
        }
        return bankRepository.save(bank);
    }

    // Actualizar un banco existente
    public Optional<Bank> updateBank(Long id, Bank updatedBank) {
        return bankRepository.findById(id).map(bank -> {
            bank.setBankName(updatedBank.getBankName());
            bank.setBankEmail(updatedBank.getBankEmail());
            bank.setBankPhone(updatedBank.getBankPhone());
            return bankRepository.save(bank);
        });
    }

    // Eliminar un banco solo si existe
    public boolean deleteBank(Long id) {
        if (bankRepository.existsById(id)) {
            bankRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener todos los bancos
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    // Obtener un banco por su ID
    public Optional<Bank> getBankById(Long id) {
        return bankRepository.findById(id);
    }

    // Buscar banco por nombre
    public Optional<Bank> getBankByName(String bankName) {
        return bankRepository.findByBankName(bankName);
    }
}
