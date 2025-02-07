package com.letrify.app.controller;

import com.letrify.app.model.Company;
import com.letrify.app.model.Individual;
import com.letrify.app.model.User;
import com.letrify.app.repository.CompanyRepository;
import com.letrify.app.repository.IndividualRepository;
import com.letrify.app.repository.UserRepository;
import com.letrify.app.util.AnsiColor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @ModelAttribute
    public void addCsrfToken(Model model, CsrfToken csrfToken) {
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
            logger.info("Token CSRF recibido: {}", csrfToken.getToken());
        }
    }
    
    @Autowired
    private UserRepository userRepository; // Repositorio para manejar usuarios

    @Autowired
    private PasswordEncoder passwordEncoder; // Para codificar las contraseñas

    @Autowired
    private CompanyRepository companyRepository; // Repositorio para manejar empresas

    @Autowired
    private IndividualRepository individualRepository; // Repositorio para manejar personas


    // Ruta para la página de inicio (index)
    @GetMapping("/")
    public String showHomePage() {
        return "index"; // Hace referencia al archivo en src/main/resources/templates/index.html
    }

    // Ruta para la página de login
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Hace referencia al archivo en src/main/resources/templates/login.html
    }

    // Ruta para la página de registro
    @GetMapping("/register")
    public String showRegisterPage(Model model, 
                                   @ModelAttribute("error") String error, 
                                   @ModelAttribute("email") String email) {
        CsrfToken csrfToken = (CsrfToken) model.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);
    
        // Asegurarse de que los atributos flash estén disponibles en la vista
        model.addAttribute("error", error);
        model.addAttribute("email", email);
    
        return "register"; // Nombre de tu plantilla Thymeleaf
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            @RequestParam("confirmPassword") String confirmPassword,
                            @RequestParam("phone_number") String phoneNumber,
                            @RequestParam("userType") String userType,
                            @RequestParam(required = false) String business_name,
                            @RequestParam(required = false) String ruc,
                            @RequestParam(required = false) String fiscal_address,
                            @RequestParam(required = false) String industry,
                            @RequestParam(required = false) String full_name,
                            @RequestParam(required = false) String dni,
                            @RequestParam(required = false) String birth_date,
                            @RequestParam(required = false) String address,
                            RedirectAttributes redirectAttributes) {

        logger.info(AnsiColor.BLUE + "Datos recibidos: Email = {}, Tipo de Usuario = {}" + AnsiColor.RESET, email, userType);

        // Verificar si el correo electrónico ya está registrado
        if (userRepository.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado. Por favor, usa otro.");
            logger.warn(AnsiColor.RED + "[ERROR] El correo '{}' ya está registrado." + AnsiColor.RESET, email);
            redirectAttributes.addFlashAttribute("userType", userType); // Guardar el tipo de usuario
            return "redirect:/register";
        }

        // Verificar si el número de teléfono está vacío
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El número de teléfono es obligatorio.");
            redirectAttributes.addFlashAttribute("userType", userType); // Guardar el tipo de usuario
            return "redirect:/register";
        }

        // Validar que el número de teléfono contenga solo números y un posible prefijo
        if (!phoneNumber.matches("^\\+?\\d{7,15}$")) {
            redirectAttributes.addFlashAttribute("error", "El número de teléfono no es válido.");
            redirectAttributes.addFlashAttribute("userType", userType); // Guardar el tipo de usuario
            return "redirect:/register";
        }

        // Agregar automáticamente el prefijo +51 si no está presente
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+51" + phoneNumber;
        }

        // Verificar si las contraseñas coinciden
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden. Intenta nuevamente.");
            logger.warn(AnsiColor.YELLOW + "[ADVERTENCIA] Las contraseñas no coinciden para el correo '{}'" + AnsiColor.RESET, email);
            redirectAttributes.addFlashAttribute("userType", userType); // Guardar el tipo de usuario
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/register";
        }

        // Guardar en la tabla 'users'
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user.setRole("USER");
        user.setEnabled(true);
        user.setUserType(User.UserType.valueOf(userType));  // Guardamos el tipo de usuario
        userRepository.save(user);

        logger.info(AnsiColor.GREEN + "[ÉXITO] Usuario registrado con éxito: {}" + AnsiColor.RESET, email);

        // Si es una empresa, guardar en la tabla 'companies'
        if ("COMPANY".equals(userType)) {
            Company company = new Company();
            company.setUser(user);
            company.setBusinessName(business_name);
            company.setRuc(ruc);
            company.setFiscalAddress(fiscal_address);
            company.setIndustry(industry);
            companyRepository.save(company);
            logger.info(AnsiColor.GREEN + "[ÉXITO] Empresa registrada: {}" + AnsiColor.RESET, business_name);
        }

        // Si es una persona, guardar en la tabla 'individuals'
        if ("INDIVIDUAL".equals(userType)) {
            Individual individual = new Individual();
            individual.setUser(user);
            individual.setFullName(full_name);
            individual.setDni(dni);
            individual.setBirthDate(birth_date);
            individual.setAddress(address);
            individualRepository.save(individual);
            logger.info(AnsiColor.GREEN + "[ÉXITO] Persona registrada: {}" + AnsiColor.RESET, full_name);
        }

        return "redirect:/login";
    }

    
    // @GetMapping("/dashboard")
    // public String showDashboardPage() {
    //     return "dashboard"; // Cargar la nueva plantilla dashboard.html
    // }
    

}