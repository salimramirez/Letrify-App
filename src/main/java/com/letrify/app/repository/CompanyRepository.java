package com.letrify.app.repository;

import com.letrify.app.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Método para buscar una empresa por RUC
    Company findByRuc(String ruc);

    boolean existsByBusinessName(String businessName);
}
