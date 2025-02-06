package com.letrify.app.repository;

import com.letrify.app.model.Individual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, Long> {

    // Método para buscar un individuo por DNI
    Individual findByDni(String dni);
}
