package com.letrify.app.controller;

import com.letrify.app.model.ExchangeRate;
import com.letrify.app.service.ExchangeRateService;
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
    public ExchangeRate getExchangeRate() {
        return exchangeRateService.getExchangeRate();
    }

    @GetMapping("/convert")
    public BigDecimal convertirMoneda(
            @RequestParam BigDecimal monto,
            @RequestParam String deMoneda,
            @RequestParam String aMoneda) {
        return exchangeRateService.convertirMoneda(monto, deMoneda, aMoneda);
    }
}
