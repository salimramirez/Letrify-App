package com.letrify.app.service;

import com.letrify.app.model.ExchangeRate;
import com.letrify.app.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://api.apis.net.pe/v1/tipo-cambio-sunat";

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, RestTemplate restTemplate) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.restTemplate = restTemplate;
    }

    public ExchangeRate getExchangeRate() {
        LocalDate today = LocalDate.now();
        Optional<ExchangeRate> existingRate = exchangeRateRepository.findByCurrencyFromAndCurrencyToAndExchangeDate("USD", "PEN", today);

        return existingRate.orElseGet(this::fetchAndSaveExchangeRate);
    }

    public ExchangeRate fetchAndSaveExchangeRate() {
        try {
            // Usar ParameterizedTypeReference para obtener un Map<String, Object> de manera segura
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
    
            Map<String, Object> response = responseEntity.getBody();
            
            if (response != null) {
                BigDecimal buyRate = BigDecimal.valueOf(Double.parseDouble(response.get("compra").toString()));
                BigDecimal sellRate = BigDecimal.valueOf(Double.parseDouble(response.get("venta").toString()));                
                String source = response.get("origen").toString();
                LocalDate exchangeDate = LocalDate.parse(response.get("fecha").toString());
    
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setCurrencyFrom("USD");
                exchangeRate.setCurrencyTo("PEN");
                exchangeRate.setBuyRate(buyRate);
                exchangeRate.setSellRate(sellRate);
                exchangeRate.setExchangeDate(exchangeDate);
                exchangeRate.setSource(source);
    
                return exchangeRateRepository.save(exchangeRate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el tipo de cambio desde la API SUNAT", e);
        }
        return null;
    }

    public BigDecimal convertirMoneda(BigDecimal monto, String deMoneda, String aMoneda) {
        ExchangeRate exchangeRate = getExchangeRate();

        if (exchangeRate == null) {
            throw new IllegalStateException("No se encontró un tipo de cambio válido.");
        }

        if (deMoneda.equalsIgnoreCase("USD") && aMoneda.equalsIgnoreCase("PEN")) {
            return monto.multiply(exchangeRate.getSellRate()); // USD → PEN usa "venta"
        } else if (deMoneda.equalsIgnoreCase("PEN") && aMoneda.equalsIgnoreCase("USD")) {
            return monto.divide(exchangeRate.getBuyRate(), 4, RoundingMode.HALF_UP); // PEN → USD usa "compra"
        } else {
            throw new IllegalArgumentException("Conversión no soportada. Solo USD y PEN están permitidos.");
        }
    }
}
