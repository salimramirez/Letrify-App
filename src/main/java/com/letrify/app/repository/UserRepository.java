package com.letrify.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.letrify.app.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Buscar usuario por correo electrónico (nuevo)
    User findByEmail(String email);

    // Nuevo método para buscar usuario por número de teléfono
    User findByPhoneNumber(String phoneNumber);

    // Recuperar User y sus relaciones en una sola consulta, evitando problemas de Lazy Loading.
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company LEFT JOIN FETCH u.individual WHERE u.id = :id")
    Optional<User> findUserWithDetailsById(@Param("id") Long id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    // Encontrar todos los User de un tipo (COMPANY o INDIVIDUAL)
    List<User> findByUserType(User.UserType userType);

    // Si quieres permitir autenticación por DNI o RUC:
    // User findByDni(String dni);        // Requiere ajuste en la entidad si lo añades directamente
    // User findByRuc(String ruc);        // Requiere ajuste en la entidad si lo añades directamente
}