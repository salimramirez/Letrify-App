// package com.letrify.app.config;

// import com.letrify.app.model.User;
// import com.letrify.app.service.UserService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class UserServiceTestConfig {

//     private static final Logger logger = LoggerFactory.getLogger(UserServiceTestConfig.class);

//     @Bean
//     CommandLineRunner testUserService(UserService userService) {
//         return args -> {
//             // try {
//             //     logger.info("Probando registro de usuario...");

//             //     User user = userService.registerUser(
//             //         "test@example.com", "999999999", "password123", "password123", // <-- Agregamos confirmPassword
//             //         User.UserType.COMPANY,
//             //         "Test Business", "12345678901", "Test Address", "Technology",
//             //         null, null, null, null, null // <-- Agregamos `other_industry` como null
//             //     );

//             //     logger.info("Usuario registrado con éxito: " + user.getEmail());
//             // } catch (Exception e) {
//             //     logger.error("Error al registrar usuario: " + e.getMessage());
//             // }
//         };
//     }
// }
