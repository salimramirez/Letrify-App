package com.letrify.app.service;

import com.letrify.app.model.BankRate;
import com.letrify.app.repository.BankRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BankRateService {

    private final BankRateRepository bankRateRepository;

    public BankRateService(BankRateRepository bankRateRepository) {
        this.bankRateRepository = bankRateRepository;
    }

    // Crear una nueva tasa para un banco
    public BankRate createBankRate(BankRate bankRate) {
        if (bankRateRepository.existsByBank_IdAndCapitalizationDays(bankRate.getBank().getId(), bankRate.getCapitalizationDays())) {
            throw new IllegalArgumentException("El banco ya tiene una tasa registrada para " + bankRate.getCapitalizationDays() + " días.");
        }
        return bankRateRepository.save(bankRate);
    }

    // Actualizar una tasa existente
    public Optional<BankRate> updateBankRate(Long id, BankRate updatedBankRate) {
        return bankRateRepository.findById(id).map(bankRate -> {
            bankRate.setNominalRate(updatedBankRate.getNominalRate());
            bankRate.setCapitalizationDays(updatedBankRate.getCapitalizationDays());
            bankRate.setEffectiveRate(updatedBankRate.getEffectiveRate());
            return bankRateRepository.save(bankRate);
        });
    }

    // Eliminar una tasa solo si existe
    public boolean deleteBankRate(Long id) {
        if (bankRateRepository.existsById(id)) {
            bankRateRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener todas las tasas
    public List<BankRate> getAllBankRates() {
        return bankRateRepository.findAll();
    }

    // Obtener una tasa por su ID
    public Optional<BankRate> getBankRateById(Long id) {
        return bankRateRepository.findById(id);
    }

    // Obtener todas las tasas de un banco específico
    public List<BankRate> getBankRatesByBankId(Long bankId) {
        return bankRateRepository.findByBank_Id(bankId);
    }
}
