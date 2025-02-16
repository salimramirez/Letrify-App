package com.letrify.app.repository;

import com.letrify.app.model.BankRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
// import java.util.Optional;

@Repository
public interface BankRateRepository extends JpaRepository<BankRate, Long> {

    // Encontrar tasas por banco
    List<BankRate> findByBank_Id(Long bankId);

    // Buscar tasas con un valor nominal mayor a un valor dado
    List<BankRate> findByNominalRateGreaterThan(Double nominalRate);

    // Buscar tasas con un período de capitalización específico
    List<BankRate> findByCapitalizationDays(Integer days);

    // Verificar si existe una tasa para un banco con un período de capitalización dado
    boolean existsByBank_IdAndCapitalizationDays(Long bankId, Integer days);
}
