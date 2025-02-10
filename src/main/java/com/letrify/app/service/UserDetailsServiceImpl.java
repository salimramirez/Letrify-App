package com.letrify.app.service;

import com.letrify.app.model.User;
import com.letrify.app.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar el usuario por correo electrónico
        User user = userRepository.findByEmail(email);
    
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }
    
        // Crear un objeto UserDetails basado en el usuario de la base de datos
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // Usar el email como identificador
                .password(user.getPassword())  // Contraseña cifrada con BCrypt
                .roles(user.getRole())  // Asignar roles (USER, ADMIN)
                .build();
    }
}