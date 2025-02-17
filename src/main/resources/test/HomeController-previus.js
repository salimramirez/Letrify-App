// package com.letrify.app.controller;

// import com.letrify.app.model.Company;
// import com.letrify.app.model.Individual;
// import com.letrify.app.model.User;
// import com.letrify.app.repository.CompanyRepository;
// import com.letrify.app.repository.IndividualRepository;
// import com.letrify.app.repository.UserRepository;
// import com.letrify.app.util.AnsiColor;

// import java.time.LocalDate;
// import java.time.format.DateTimeParseException;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// import org.springframework.security.web.csrf.CsrfToken;

// @Controller
// public class HomeController {

//     private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

//     @ModelAttribute
//     public void addCsrfToken(Model model, CsrfToken csrfToken) {
//         if (csrfToken != null) {
//             model.addAttribute("_csrf", csrfToken);
//             logger.info("Token CSRF recibido: {}", csrfToken.getToken());
//         }
//     }
    
//     @Autowired
//     private UserRepository userRepository; // Repositorio para manejar usuarios

//     @Autowired
//     private PasswordEncoder passwordEncoder; // Para codificar las contraseñas

//     @Autowired
//     private CompanyRepository companyRepository; // Repositorio para manejar empresas

//     @Autowired
//     private IndividualRepository individualRepository; // Repositorio para manejar personas


//     // Ruta para la página de inicio (index)
//     @GetMapping("/")
//     public String showHomePage() {
//         return "index"; // Hace referencia al archivo en src/main/resources/templates/index.html
//     }

//     // Ruta para la página de login
//     @GetMapping("/login")
//     public String showLoginPage() {
//         return "login"; // Hace referencia al archivo en src/main/resources/templates/login.html
//     }

//     // Ruta para la página de registro
//     @GetMapping("/register")
//     public String showRegisterPage(Model model, 
//                                    @ModelAttribute("error") String error, 
//                                    @ModelAttribute("email") String email) {
//         CsrfToken csrfToken = (CsrfToken) model.getAttribute("_csrf");
//         model.addAttribute("_csrf", csrfToken);
    
//         // Asegurarse de que los atributos flash estén disponibles en la vista
//         model.addAttribute("error", error);
//         model.addAttribute("email", email);
    
//         return "register"; // Nombre de tu plantilla Thymeleaf
//     }

//     @PostMapping("/register")
//     public String registerUser(@RequestParam("password") String password,
//                             @RequestParam("confirmPassword") String confirmPassword,
//                             @RequestParam("userType") String userType,
//                             @RequestParam(required = false) String emailEmpresa,
//                             @RequestParam(required = false) String emailPersona,
//                             @RequestParam(required = false) String phoneEmpresa,
//                             @RequestParam(required = false) String phonePersona,
//                             @RequestParam(required = false) String business_name,
//                             @RequestParam(required = false) String ruc,
//                             @RequestParam(required = false) String fiscal_address,
//                             @RequestParam(required = false) String industry,
//                             @RequestParam(required = false) String other_industry, // Capturamos el input adicional
//                             @RequestParam(required = false) String full_name,
//                             @RequestParam(required = false) String dni,
//                             @RequestParam(required = false) String birth_date,
//                             @RequestParam(required = false) String address,
//                             RedirectAttributes redirectAttributes) {

//         // ###################################
//         // ##### VALIDACIONES GENERALES ######
//         // ###################################

//         // Determinar el email y teléfono según el tipo de usuario
//         String email = "COMPANY".equals(userType) ? emailEmpresa : emailPersona;
//         String phoneNumber = "COMPANY".equals(userType) ? phoneEmpresa : phonePersona;

//         // Validar número de teléfono
//         String phoneWithoutPrefix = phoneNumber.startsWith("+51") ? phoneNumber.substring(3) : phoneNumber;
//         if (phoneNumber.startsWith("+") || !isValidPhone(phoneNumber)) {
//             String errorMessage = phoneNumber.startsWith("+") 
//                 ? "No debes ingresar el prefijo (+51)." 
//                 : "Número de teléfono no válido. Debe tener exactamente 9 dígitos.";
            
//             redirectAttributes.addFlashAttribute("error", errorMessage);
//             redirectAttributes.addFlashAttribute("userType", userType);
        
//             if ("COMPANY".equals(userType)) {
//                 redirectAttributes.addFlashAttribute("invalidPhoneEmpresa", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//             } else {
//                 redirectAttributes.addFlashAttribute("invalidPhonePersona", true);
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
//             }
        
//             return "redirect:/register";
//         }

//         // Agregar prefijo +51 para la verificación y almacenamiento
//         String phoneWithPrefix = "+51" + phoneNumber;
        
//         // Verificar si el número de teléfono ya está registrado
//         if (userRepository.findByPhoneNumber(phoneWithPrefix) != null) {
//             redirectAttributes.addFlashAttribute("error", "El número de teléfono ya está registrado. Por favor, usa otro.");
//             redirectAttributes.addFlashAttribute("userType", userType);
        
//             if ("COMPANY".equals(userType)) {
//                 redirectAttributes.addFlashAttribute("invalidPhoneEmpresa", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//             } else {
//                 redirectAttributes.addFlashAttribute("invalidPhonePersona", true);
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
//             }
        
//             return "redirect:/register";
//         }

//         // Guardar el número de teléfono con el prefijo +51
//         phoneNumber = phoneWithPrefix;

//         // Validar correo
//         if (!isValidEmail(email)) {
//             redirectAttributes.addFlashAttribute("error", "Correo electrónico no válido.");
//             redirectAttributes.addFlashAttribute("userType", userType);
            
//             if ("COMPANY".equals(userType)) {
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//             } else {
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
//             }
            
//             return "redirect:/register";
//         }

//         logger.info(AnsiColor.BLUE + "Datos recibidos: Email = {}, Tipo de Usuario = {}" + AnsiColor.RESET, email, userType);
    
//         // Log para verificar los valores recibidos
//         logger.info(AnsiColor.RED + "[DEBUG] Sector recibido (industry): {}" + AnsiColor.RESET, industry);
//         logger.info(AnsiColor.RED + "[DEBUG] Otro sector recibido (other_industry): {}" + AnsiColor.RESET, other_industry);

//         // Validar que la contraseña sea válida y coincida con la confirmación
//         if (!isValidPassword(password) || !password.equals(confirmPassword)) {
//             if (!isValidPassword(password)) {
//                 redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres y contener letras y números.");
//                 logger.warn(AnsiColor.YELLOW + "[ADVERTENCIA] Contraseña inválida para el correo '{}'" + AnsiColor.RESET, email);
//             } else {
//                 redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden. Intenta nuevamente.");
//                 logger.warn(AnsiColor.YELLOW + "[ADVERTENCIA] Las contraseñas no coinciden para el correo '{}'" + AnsiColor.RESET, email);
//             }
        
//             redirectAttributes.addFlashAttribute("userType", userType);
            
//             if ("COMPANY".equals(userType)) {
//                 redirectAttributes.addFlashAttribute("invalidPasswordEmpresa", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//             } else {
//                 redirectAttributes.addFlashAttribute("invalidPasswordPersona", true);
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
//             }
        
//             return "redirect:/register";
//         }

//         // ####################################
//         // ##### VALIDACIONES DE EMPRESA ######
//         // ####################################

//         // Verificar si la Razón Social ya está registrada antes de insertar en la base de datos
//         if ("COMPANY".equals(userType)) {
//             if (business_name == null || business_name.trim().isEmpty()) {
//                 redirectAttributes.addFlashAttribute("error", "La Razón Social es obligatoria.");
//                 redirectAttributes.addFlashAttribute("invalidBusinessName", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
        
//             if (business_name.trim().length() < 5 || business_name.trim().length() > 100) {
//                 redirectAttributes.addFlashAttribute("error", "La Razón Social debe tener entre 5 y 100 caracteres.");
//                 redirectAttributes.addFlashAttribute("invalidBusinessName", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
        
//             // Verificar si la Razón Social ya está registrada antes de insertar en la base de datos
//             if (companyRepository.existsByBusinessName(business_name)) {
//                 redirectAttributes.addFlashAttribute("error", "La Razón Social ya está registrada. Por favor, usa otra.");
//                 redirectAttributes.addFlashAttribute("invalidBusinessName", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
//         }

//         // Verificar RUC
//         if ("COMPANY".equals(userType)) {
//             if (ruc == null || ruc.trim().isEmpty()) {
//                 redirectAttributes.addFlashAttribute("error", "El RUC es obligatorio.");
//                 redirectAttributes.addFlashAttribute("invalidRuc", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
        
//             if (!ruc.matches("\\d{11}")) {
//                 redirectAttributes.addFlashAttribute("error", "El RUC debe contener exactamente 11 dígitos.");
//                 redirectAttributes.addFlashAttribute("invalidRuc", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
        
//             // Verificar si el RUC ya está registrado en la base de datos
//             if (companyRepository.findByRuc(ruc) != null) {
//                 redirectAttributes.addFlashAttribute("error", "El RUC ya está registrado. Por favor, verifica tu información.");
//                 redirectAttributes.addFlashAttribute("invalidRuc", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
//         }

//         // Validar dirección fiscal: Debe tener al menos 5 caracteres
//         if ("COMPANY".equals(userType)) {
//             if (fiscal_address == null || fiscal_address.trim().length() < 5) {
//                 redirectAttributes.addFlashAttribute("error", "La Dirección Fiscal debe tener al menos 5 caracteres.");
//                 redirectAttributes.addFlashAttribute("invalidFiscalAddress", true);
                
//                 // Conservar los demás datos
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);
                
//                 return "redirect:/register";
//             }
//         }

//         // Verificar si el correo electrónico ya está registrado
//         if (userRepository.findByEmail(email) != null) {
//             redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado. Por favor, usa otro.");
//             redirectAttributes.addFlashAttribute("userType", userType);
            
//             if ("COMPANY".equals(userType)) {
//                 redirectAttributes.addFlashAttribute("invalidEmailEmpresa", true);
//                 redirectAttributes.addFlashAttribute("business_name", business_name);
//                 redirectAttributes.addFlashAttribute("ruc", ruc);
//                 redirectAttributes.addFlashAttribute("fiscal_address", fiscal_address);
//                 redirectAttributes.addFlashAttribute("emailEmpresa", email);
//                 redirectAttributes.addFlashAttribute("phoneEmpresa", phoneWithoutPrefix);  // Conservar phoneEmpresa
//             } else {
//                 redirectAttributes.addFlashAttribute("invalidEmailPersona", true);
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);  // Conservar phonePersona
//             }
            
//             logger.warn(AnsiColor.RED + "[ERROR] El correo '{}' ya está registrado." + AnsiColor.RESET, email);
//             return "redirect:/register";
//         }

//         // ####################################
//         // ##### VALIDACIONES DE PERSONA ######
//         // ####################################

//         // Validación del Nombre Completo para INDIVIDUAL
//         if ("INDIVIDUAL".equals(userType)) {
//             if (full_name == null || full_name.trim().length() < 8) {
//                 redirectAttributes.addFlashAttribute("error", "El nombre completo debe tener al menos 8 caracteres.");
                
//                 // Marcar el campo como inválido
//                 redirectAttributes.addFlashAttribute("invalidFullName", true);
                
//                 // Conservar todos los demás datos ingresados
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
                
//                 return "redirect:/register";
//             }
//         }

//         // Validación del DNI para INDIVIDUAL
//         if ("INDIVIDUAL".equals(userType)) {
//             if (dni == null || !dni.matches("\\d{8}")) {
//                 redirectAttributes.addFlashAttribute("error", "El DNI debe contener exactamente 8 dígitos.");
//                 redirectAttributes.addFlashAttribute("invalidDni", true);
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
            
//             // Verificar si el DNI ya está registrado en la base de datos
//             if (individualRepository.existsByDni(dni)) {
//                 redirectAttributes.addFlashAttribute("error", "El DNI ya está registrado. Verifica tu información.");
//                 redirectAttributes.addFlashAttribute("invalidDni", true);
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
//         }

//         // Validación de fecha de nacimiento para INDIVIDUAL
//         if ("INDIVIDUAL".equals(userType)) {
//             boolean hasError = false;  // Bandera para verificar si encontramos un error en esta sección.
        
//             if (birth_date == null || birth_date.isEmpty()) {
//                 redirectAttributes.addFlashAttribute("error", "La fecha de nacimiento es obligatoria.");
//                 redirectAttributes.addFlashAttribute("invalidBirthDate", true);
//                 hasError = true;
//             } else {
//                 try {
//                     LocalDate birthDateParsed = LocalDate.parse(birth_date);
//                     LocalDate today = LocalDate.now();
//                     LocalDate minValidBirthDate = today.minusYears(18);  // Fecha mínima válida: hace 18 años desde hoy
        
//                     if (birthDateParsed.isAfter(today)) {
//                         redirectAttributes.addFlashAttribute("error", "La fecha de nacimiento no puede ser en el futuro.");
//                         redirectAttributes.addFlashAttribute("invalidBirthDate", true);
//                         hasError = true;
//                     } else if (birthDateParsed.isAfter(minValidBirthDate)) {
//                         redirectAttributes.addFlashAttribute("error", "Debes tener al menos 18 años para registrarte.");
//                         redirectAttributes.addFlashAttribute("invalidBirthDate", true);
//                         hasError = true;
//                     }
//                 } catch (DateTimeParseException e) {
//                     redirectAttributes.addFlashAttribute("error", "Formato de fecha de nacimiento no válido.");
//                     redirectAttributes.addFlashAttribute("invalidBirthDate", true);
//                     hasError = true;
//                 }
//             }
        
//             // Si se detectó algún error, conservar los datos y redirigir
//             if (hasError) {
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address);
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
//                 return "redirect:/register";
//             }
//         }

//         // Validación de dirección para INDIVIDUAL
//         if ("INDIVIDUAL".equals(userType)) {
//             if (address == null || address.trim().isEmpty() || address.trim().length() < 5) {
//                 redirectAttributes.addFlashAttribute("error", "La dirección debe tener al menos 5 caracteres.");
                
//                 // Marcar el campo como inválido
//                 redirectAttributes.addFlashAttribute("invalidAddress", true);
                
//                 // Conservar todos los demás datos ingresados
//                 redirectAttributes.addFlashAttribute("full_name", full_name);
//                 redirectAttributes.addFlashAttribute("dni", dni);
//                 redirectAttributes.addFlashAttribute("birth_date", birth_date);
//                 redirectAttributes.addFlashAttribute("address", address); // Conservar la dirección ingresada
//                 redirectAttributes.addFlashAttribute("userType", userType);
//                 redirectAttributes.addFlashAttribute("emailPersona", email);
//                 redirectAttributes.addFlashAttribute("phonePersona", phoneWithoutPrefix);
                
//                 return "redirect:/register";
//             }
//         }

//         // Si se ingresó un sector en "Otro", usarlo en lugar del select
//         if (industry != null && industry.equals("Otro") && other_industry != null && !other_industry.trim().isEmpty()) {
//             industry = other_industry.trim();
//         }

//         // #############################
//         // ##### GUARDAR USUARIOS ######
//         // #############################

//         // Guardar en la tabla 'users'
//         User user = new User();
//         user.setEmail(email);
//         user.setPassword(passwordEncoder.encode(password));
//         user.setPhoneNumber(phoneNumber);
//         user.setRole("USER");
//         user.setEnabled(true);
//         user.setUserType(User.UserType.valueOf(userType));  // Guardamos el tipo de usuario
//         userRepository.save(user);

//         logger.info(AnsiColor.GREEN + "[ÉXITO] Usuario registrado con éxito: {}" + AnsiColor.RESET, email);

//         // Si es una empresa, guardar en la tabla 'companies'
//         if ("COMPANY".equals(userType)) {
//             Company company = new Company();
//             company.setUser(user);
//             company.setBusinessName(business_name);
//             company.setRuc(ruc);
//             company.setFiscalAddress(fiscal_address);
//             company.setIndustry(industry); // Guardar el sector final (ya sea el del select o el input)
//             companyRepository.save(company);
//             logger.info(AnsiColor.GREEN + "[ÉXITO] Empresa registrada: {}" + AnsiColor.RESET, business_name);
//         }

//         // Si es una persona, guardar en la tabla 'individuals'
//         if ("INDIVIDUAL".equals(userType)) {
//             Individual individual = new Individual();
//             individual.setUser(user);
//             individual.setFullName(full_name);
//             individual.setDni(dni);
//             individual.setBirthDate(birth_date);
//             individual.setAddress(address);
//             individualRepository.save(individual);
//             logger.info(AnsiColor.GREEN + "[ÉXITO] Persona registrada: {}" + AnsiColor.RESET, full_name);
//         }

//         // ########################
//         // ##### REDIRECCIÓN ######
//         // ########################

//         // Agregar mensaje de éxito y redirigir al login
//         redirectAttributes.addFlashAttribute("success", "Usuario registrado con éxito.");
//         logger.info(AnsiColor.GREEN + "[ÉXITO] Usuario registrado correctamente: {}" + AnsiColor.RESET, email);

//         return "redirect:/login";
//     }

    
//     // @GetMapping("/dashboard")
//     // public String showDashboardPage() {
//     //     return "dashboard"; // Cargar la nueva plantilla dashboard.html
//     // }

//     private boolean isValidEmail(String email) {
//         String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
//         return email != null && email.matches(emailRegex);
//     }
    
//     private boolean isValidPhone(String phone) {
//         String phoneRegex = "^\\d{9}$";
//         return phone != null && phone.matches(phoneRegex);
//     }
    
//     // Método para validar la contraseña
//     private boolean isValidPassword(String password) {
//         // Al menos 6 caracteres, debe contener al menos una letra y un número
//         String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
//         return password != null && password.matches(passwordRegex);
//     }

// }