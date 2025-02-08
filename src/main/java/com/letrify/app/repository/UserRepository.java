package com.letrify.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.letrify.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Buscar usuario por correo electrónico (nuevo)
    User findByEmail(String email);

    // Nuevo método para buscar usuario por número de teléfono
    User findByPhoneNumber(String phoneNumber);
    
    // Si quieres permitir autenticación por DNI o RUC:
    // User findByDni(String dni);        // Requiere ajuste en la entidad si lo añades directamente
    // User findByRuc(String ruc);        // Requiere ajuste en la entidad si lo añades directamente
}