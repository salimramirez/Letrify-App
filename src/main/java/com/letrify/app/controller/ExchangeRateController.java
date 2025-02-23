package com.letrify.app.controller;

import com.letrify.app.model.ExchangeRate;
import com.letrify.app.service.ExchangeRateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public ResponseEntity<ExchangeRate> getExchangeRate() {
        ExchangeRate exchangeRate = exchangeRateService.getExchangeRate();
        if (exchangeRate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exchangeRate);
    }

    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convertirMoneda(
            @RequestParam BigDecimal monto,
            @RequestParam String deMoneda,
            @RequestParam String aMoneda) {
        try {
            BigDecimal resultado = exchangeRateService.convertirMoneda(monto, deMoneda, aMoneda);
            return ResponseEntity.ok(resultado);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null); // Si no hay tipo de cambio registrado
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Si la conversión no es válida
        }
    }
}
