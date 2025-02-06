package com.letrify.app.service;

import com.letrify.app.model.User;
import com.letrify.app.model.Individual;
import com.letrify.app.model.Company;
import com.letrify.app.repository.UserRepository;
import com.letrify.app.repository.IndividualRepository;
import com.letrify.app.repository.CompanyRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final IndividualRepository individualRepository;
    private final CompanyRepository companyRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  IndividualRepository individualRepository,
                                  CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.individualRepository = individualRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = null;

        if (identifier.contains("@")) {
            // Autenticación por correo electrónico
            user = userRepository.findByEmail(identifier);
        } else if (identifier.matches("\\d{8}")) {
            // Autenticación por DNI (8 dígitos)
            Individual individual = individualRepository.findByDni(identifier);
            if (individual != null) {
                user = individual.getUser();
            }
        } else if (identifier.matches("\\d{11}")) {
            // Autenticación por RUC (11 dígitos)
            Company company = companyRepository.findByRuc(identifier);
            if (company != null) {
                user = company.getUser();
            }
        }

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con identificador: " + identifier);
        }

        // Crear un objeto UserDetails basado en el usuario de la base de datos
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // Siempre usar el correo como identificador en la sesión
                .password(user.getPassword())
                .roles(user.getRole())  // Asignar roles (USER, ADMIN)
                .build();
    }
}