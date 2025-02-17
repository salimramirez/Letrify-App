package com.letrify.app.service;

import com.letrify.app.model.Company;
import com.letrify.app.model.Individual;
import com.letrify.app.model.User;
import com.letrify.app.repository.CompanyRepository;
import com.letrify.app.repository.IndividualRepository;
import com.letrify.app.repository.UserRepository;
import com.letrify.app.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final IndividualRepository individualRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       IndividualRepository individualRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.individualRepository = individualRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String email, String phoneNumber, String password, String confirmPassword, User.UserType userType,
                             String businessName, String ruc, String fiscalAddress, String industry, String otherIndustry,
                             String fullName, String dni, String birthDate, String address) {
        
        // Validar formato de correo electrónico
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Correo electrónico no válido.");
        }
                                
        // Validar si el email ya está en uso
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("El correo ya está registrado. Por favor, usa otro.");
        }

        // Validar la contraseña
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres y contener letras y números.");
        }
        
        // Validar si la contraseña y la confirmación coinciden
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden. Intenta nuevamente.");
        }
        
        // Validar número de teléfono (sin prefijo y solo 9 dígitos)
        if (phoneNumber.startsWith("+")) {
            throw new IllegalArgumentException("No debes ingresar el prefijo (+51)");
        }
        if (!phoneNumber.matches("\\d{9}")) {
            throw new IllegalArgumentException("Número de teléfono no válido. Debe tener exactamente 9 dígitos");
        }

        // Agregar prefijo +51 antes de guardarlo
        String formattedPhone = "+51" + phoneNumber;

        // Validar si el número de teléfono ya está en uso
        if (userRepository.findByPhoneNumber(formattedPhone) != null) {
            throw new IllegalArgumentException("El número de teléfono ya está registrado.");
        }

        // Crear el usuario
        User user = new User();
        user.setEmail(email);
        user.setPhoneNumber(formattedPhone);
        user.setPassword(passwordEncoder.encode(password)); // Encriptar contraseña
        user.setUserType(userType);
        user.setEnabled(true);

        // Guardar usuario en la base de datos
        user = userRepository.save(user);

        // Validar y registrar Empresa (Company)
        if (userType == User.UserType.COMPANY) {
            validateBusinessName(businessName);
            validateRUC(ruc);
            validateFiscalAddress(fiscalAddress);
    
            // Verificar si se seleccionó "Otro" en el sector y se ingresó un sector personalizado
            String finalIndustry = industry != null && industry.equalsIgnoreCase("Otro") && otherIndustry != null && !otherIndustry.trim().isEmpty()
                    ? otherIndustry.trim()
                    : industry; // Si no, usar el sector seleccionado.
    
            Company company = new Company();
            company.setUser(user);
            company.setBusinessName(businessName);
            company.setRuc(ruc);
            company.setFiscalAddress(fiscalAddress);
            company.setIndustry(finalIndustry);
            companyRepository.save(company);
        }
        
        // Validar y registrar Persona (Individual)
        else if (userType == User.UserType.INDIVIDUAL) {
            validateFullName(fullName);
            validateDNI(dni);
            validateBirthDate(birthDate);
            validateAddress(address);
    
            Individual individual = new Individual();
            individual.setUser(user);
            individual.setFullName(fullName);
            individual.setDni(dni);
            individual.setBirthDate(birthDate);
            individual.setAddress(address);
            individualRepository.save(individual);
        }
        
        return user;
    }

    // Método para validar formato de correo
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email != null && email.matches(emailRegex);
    }

    // Método para validar formato de contraseña
    private boolean isValidPassword(String password) {
        // Al menos 6 caracteres, debe contener al menos una letra y un número
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
        return password != null && password.matches(passwordRegex);
    }

    // Método para validar la razón social
    private void validateBusinessName(String businessName) {
        if (businessName == null || businessName.trim().isEmpty()) {
            throw new IllegalArgumentException("La Razón Social es obligatoria.");
        }
    
        if (businessName.trim().length() < 5 || businessName.trim().length() > 100) {
            throw new IllegalArgumentException("La Razón Social debe tener entre 5 y 100 caracteres.");
        }
    
        if (companyRepository.existsByBusinessName(businessName)) {
            throw new IllegalArgumentException("La Razón Social ya está registrada. Por favor, usa otra.");
        }
    }

    // Método para validar el RUC
    private void validateRUC(String ruc) {
        if (ruc == null || ruc.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUC es obligatorio.");
        }
    
        if (!ruc.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe contener exactamente 11 dígitos.");
        }
    
        if (companyRepository.findByRuc(ruc) != null) {
            throw new IllegalArgumentException("El RUC ya está registrado. Por favor, verifica tu información.");
        }
    }

    // Método para validar la Dirección Fiscal
    private void validateFiscalAddress(String fiscalAddress) {
        if (fiscalAddress == null || fiscalAddress.trim().length() < 5) {
            throw new IllegalArgumentException("La Dirección Fiscal debe tener al menos 5 caracteres.");
        }
    }

    // Método para validar el Nombre Completo
    private void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().length() < 8) {
            throw new IllegalArgumentException("El Nombre Completo debe tener al menos 8 caracteres.");
        }
    }

    // Método para validar el DNI
    private void validateDNI(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos.");
        }
    
        if (individualRepository.existsByDni(dni)) {
            throw new IllegalArgumentException("El DNI ya está registrado. Verifica tu información.");
        }
    }

    // Método para validar la Fecha de Nacimiento
    private void validateBirthDate(String birthDate) {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }

        try {
            LocalDate birthDateParsed = LocalDate.parse(birthDate);
            LocalDate today = LocalDate.now();
            LocalDate minValidBirthDate = today.minusYears(18);  // Edad mínima: 18 años

            if (birthDateParsed.isAfter(today)) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede estar en el futuro.");
            } else if (birthDateParsed.isAfter(minValidBirthDate)) {
                throw new IllegalArgumentException("Debes tener al menos 18 años para registrarte.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha de nacimiento no válido. Usa el formato YYYY-MM-DD.");
        }
    }

    // Método para validar la Dirección
    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty() || address.trim().length() < 5) {
            throw new IllegalArgumentException("La dirección debe tener al menos 5 caracteres.");
        }
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = findUserById(id);
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setRole(updatedUser.getRole());
        return userRepository.save(user);
    }

    public void disableUser(Long id) {
        User user = findUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }
}
