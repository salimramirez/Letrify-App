package com.letrify.app.repository;

import com.letrify.app.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;        // Importar List
import java.util.Optional;    // Importar Optional

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    // Buscar banco por nombre (exacto o parcial)
    List<Bank> findByBankNameContainingIgnoreCase(String bankName);

    // Verificar si existe un banco por su email
    boolean existsByBankEmail(String bankEmail);

    // Buscar banco por número de teléfono
    Optional<Bank> findByBankPhone(String bankPhone);
}
