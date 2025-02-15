package com.letrify.app.repository;

import com.letrify.app.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    // Encontrar banco por nombre (coincidencia exacta)
    Optional<Bank> findByBankName(String bankName);

    // Buscar bancos cuyo nombre contenga una palabra clave (ignora mayúsculas/minúsculas)
    List<Bank> findByBankNameContainingIgnoreCase(String keyword);

    // Encontrar banco por correo electrónico (coincidencia exacta)
    Optional<Bank> findByBankEmail(String bankEmail);

    // Verificar si existe un banco con un nombre específico
    boolean existsByBankName(String bankName);

    // Verificar si existe un banco con un correo electrónico específico
    boolean existsByBankEmail(String bankEmail);
}
