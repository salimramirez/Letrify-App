package com.letrify.app.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejar errores 403 (Acceso denegado)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // Código de estado 403
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("error", "No tienes permisos para acceder a esta página.");
        return "403"; // Nombre de la plantilla Thymeleaf (403.html)
    }

    // Manejar errores genéricos (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Código de estado 500
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("error", "Ocurrió un error inesperado. Por favor, intenta más tarde.");
        model.addAttribute("message", ex.getMessage()); // Puedes mostrar el mensaje si es útil
        return "500"; // Nombre de la plantilla Thymeleaf (500.html)
    }

    // Manejar errores 404 (Página no encontrada)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "La página solicitada no fue encontrada.");
        return "404"; // Página personalizada 404.html
    }

}
