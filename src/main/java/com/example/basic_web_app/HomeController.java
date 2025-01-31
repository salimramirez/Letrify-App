package com.example.basic_web_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String showRegisterPage(Model model) {
        CsrfToken csrfToken = (CsrfToken) model.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);
        return "register"; // Nombre de tu plantilla Thymeleaf
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        // Log para verificar si el controlador recibe los datos
        logger.info("Datos recibidos: {}, {}", user.getUsername(), user.getEmail());
    
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "El usuario ya existe.");
            System.out.println("Error: El usuario ya existe");
            return "register";
        }
    
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setEnabled(true);
    
        // Log para confirmar que el usuario será guardado
        System.out.println("Guardando usuario: " + user);
    
        userRepository.save(user);
    
        System.out.println("Usuario registrado con éxito");
        return "redirect:/login";
    }
    
    @GetMapping("/dashboard")
    public String showDashboardPage() {
        return "dashboard"; // Cargar la nueva plantilla dashboard.html
    }
    

}