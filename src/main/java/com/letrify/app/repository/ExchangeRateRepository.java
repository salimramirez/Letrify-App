package com.letrify.app.repository;

import com.letrify.app.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByCurrencyFromAndCurrencyToAndExchangeDate(String currencyFrom, String currencyTo, LocalDate exchangeDate);
}
