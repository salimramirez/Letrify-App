package com.letrify.app.controller;

import com.letrify.app.model.User;
import com.letrify.app.service.UserService;
import com.letrify.app.util.AnsiColor;

import jakarta.annotation.PostConstruct;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
public class TimezoneChecker {
    @PostConstruct
    public void init() {
        System.out.println("Zona horaria del sistema: " + TimeZone.getDefault().getID());
        System.out.println("Zona horaria de Java: " + ZoneId.systemDefault());
        System.out.println("Fecha y hora en UTC: " + ZonedDateTime.now(ZoneId.of("UTC")));
        System.out.println("Fecha y hora en la zona del sistema: " + ZonedDateTime.now());
    }
}

    @Autowired
    private UserService userService; // Nuevo servicio para manejar usuarios

    @ModelAttribute
    public void addCsrfToken(Model model, CsrfToken csrfToken) {
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
            logger.info("Token CSRF recibido: {}", csrfToken.getToken());
        }
    }

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
    public String registerUser(@RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           @RequestParam("userType") String userType,
                           @RequestParam(required = false) String emailEmpresa,
                           @RequestParam(required = false) String emailPersona,
                           @RequestParam(required = false) String phoneEmpresa,
                           @RequestParam(required = false) String phonePersona,
                           @RequestParam(required = false) String business_name,
                           @RequestParam(required = false) String ruc,
                           @RequestParam(required = false) String fiscal_address,
                           @RequestParam(required = false) String industry,
                           @RequestParam(required = false) String other_industry, // Capturamos el input adicional
                           @RequestParam(required = false) String full_name,
                           @RequestParam(required = false) String dni,
                           @RequestParam(required = false) String birth_date,
                           @RequestParam(required = false) String address,
                           RedirectAttributes redirectAttributes) {

        // ###################################
        // ##### VALIDACIONES GENERALES ######
        // ###################################

        try {
            // Determinar email y teléfono según el tipo de usuario
            String email = "COMPANY".equalsIgnoreCase(userType) ? emailEmpresa : emailPersona;
            String phoneNumber = "COMPANY".equalsIgnoreCase(userType) ? phoneEmpresa : phonePersona;
    
            // Convertir el tipo de usuario
            User.UserType parsedUserType;
            try {
                parsedUserType = User.UserType.valueOf(userType.toUpperCase());
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Tipo de usuario inválido.");
                return "redirect:/register";
            }
    
            // Log para depuración
            logger.info(AnsiColor.BLUE + "[DEBUG] Datos recibidos: Email = {}, Tipo de Usuario = {}" + AnsiColor.RESET, email, userType);
            logger.info(AnsiColor.RED + "[DEBUG] Sector recibido (industry): {}" + AnsiColor.RESET, industry);
            logger.info(AnsiColor.RED + "[DEBUG] Otro sector recibido (other_industry): {}" + AnsiColor.RESET, other_industry);
    
            // Llamar a UserService para manejar el registro
            userService.registerUser(email, phoneNumber, password, confirmPassword, parsedUserType,
                    business_name, ruc, fiscal_address, industry, other_industry, full_name, dni, birth_date, address);
    
            // Mensaje de éxito y redirección al login
            redirectAttributes.addFlashAttribute("success", "Usuario registrado con éxito.");
            logger.info(AnsiColor.GREEN + "[ÉXITO] Usuario registrado correctamente: {}" + AnsiColor.RESET, email);
            return "redirect:/login";
    
        } catch (IllegalArgumentException e) {
            // Manejar errores de validación y redirigir de vuelta al formulario de registro
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("userType", userType);
            return "redirect:/register";
        }
    }

    // @GetMapping("/dashboard")
    // public String showDashboardPage() {
    //     return "dashboard"; // Cargar la nueva plantilla dashboard.html
    // }

}