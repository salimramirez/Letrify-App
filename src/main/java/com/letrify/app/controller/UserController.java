package com.letrify.app.controller;

import com.letrify.app.model.User;
import com.letrify.app.service.CustomUserDetails;
import com.letrify.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        // Obtener los detalles del usuario autenticado desde Spring Security
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Buscar al usuario en la base de datos para incluir detalles adicionales
        User user = userService.findUserByEmail(userDetails.getUsername());

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Construir la respuesta con la información relevante
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userDetails.getUserId());
        response.put("email", userDetails.getUsername());
        response.put("userType", userDetails.getUserType());
        response.put("companyId", userDetails.getCompanyId());
        response.put("individualId", userDetails.getIndividualId());

        return ResponseEntity.ok(response);
    }
}
