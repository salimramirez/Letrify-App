package com.letrify.app.repository;

import com.letrify.app.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Método para buscar una empresa por RUC
    Company findByRuc(String ruc);

    // Buscar una empresa a partir de un userId
    Company findByUserId(Long userId);

    boolean existsByBusinessName(String businessName);

    // Recuperar la empresa y su usuario en una sola consulta
    @Query("SELECT c FROM Company c JOIN FETCH c.user WHERE c.id = :id")
    Company findCompanyWithUserById(@Param("id") Long id);

    // Listar todas las empresas de un sector específico
    // List<Company> findByIndustry(String industry);

}
