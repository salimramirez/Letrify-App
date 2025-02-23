package com.letrify.app.repository;

import com.letrify.app.model.BankFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankFeeRepository extends JpaRepository<BankFee, Long> {

    // Obtener todas las tarifas de un banco específico
    List<BankFee> findByBank_Id(Long bankId);
}
