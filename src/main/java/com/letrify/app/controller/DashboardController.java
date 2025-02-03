package com.letrify.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String mostrarFormulario() {
        return "dashboard"; // Esto carga el HTML que creamos
    }

    @PostMapping("/dashboard")
    public String procesarSuma(@RequestParam("num1") int num1,
                               @RequestParam("num2") int num2,
                               Model model) {
        int resultado = num1 + num2;
        model.addAttribute("resultado", resultado);
        return "dashboard"; // Vuelve a la misma vista mostrando el resultado
    }
}
