package com.letrify.app.repository;

import com.letrify.app.model.Individual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, Long> {

    // Método para buscar un individuo por DNI
    Individual findByDni(String dni);

    // Buscar a una empresa a partir de un UserID
    Individual findByUserId(Long userId);

    boolean existsByDni(String dni);

    // Recuperar el individuo y su usuario en una sola consulta
    @Query("SELECT i FROM Individual i JOIN FETCH i.user WHERE i.id = :id")
    Individual findIndividualWithUserById(@Param("id") Long id);

    // Buscar individuos por nombre
    // List<Individual> findByFullNameContainingIgnoreCase(String fullName);

}
