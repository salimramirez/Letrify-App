package com.letrify.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.letrify.app.model.Discount.RateType;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @Column(name = "bank_name", nullable = false, length = 255)
    private String bankName;

    @Column(name = "discount_date", nullable = false)
    private LocalDate discountDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false)
    private RateType rateType;

    @Column(name = "rate", precision = 15, scale = 9, nullable = false)
    private BigDecimal rate;

    @Column(name = "rate_days", nullable = false)
    private Integer rateDays;

    @Column(name = "capitalization_days")
    private Integer capitalizationDays;

    @Column(name = "total_discount_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalDiscountAmount;

    @Column(name = "interest_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal interestAmount;

    @Column(name = "tcea", precision = 15, scale = 9, nullable = false)
    private BigDecimal tcea;

    @Column(name = "exchange_rate", precision = 10, scale = 5)
    private BigDecimal exchangeRate;

    @Column(name = "report_generated_at", nullable = false)
    private LocalDateTime reportGeneratedAt = LocalDateTime.now();

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public LocalDate getDiscountDate() {
        return discountDate;
    }

    public void setDiscountDate(LocalDate discountDate) {
        this.discountDate = discountDate;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getRateDays() {
        return rateDays;
    }

    public void setRateDays(Integer rateDays) {
        this.rateDays = rateDays;
    }

    public Integer getCapitalizationDays() {
        return capitalizationDays;
    }

    public void setCapitalizationDays(Integer capitalizationDays) {
        this.capitalizationDays = capitalizationDays;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public BigDecimal getTcea() {
        return tcea;
    }

    public void setTcea(BigDecimal tcea) {
        this.tcea = tcea;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public LocalDateTime getReportGeneratedAt() {
        return reportGeneratedAt;
    }

    public void setReportGeneratedAt(LocalDateTime reportGeneratedAt) {
        this.reportGeneratedAt = reportGeneratedAt;
    }
}
