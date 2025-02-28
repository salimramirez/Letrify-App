package com.letrify.app.service;

import com.letrify.app.model.Bank;
import com.letrify.app.model.Discount;
import com.letrify.app.model.DiscountFee;
import com.letrify.app.model.Document;
import com.letrify.app.model.DocumentDiscount;
import com.letrify.app.model.ExchangeRate;
import com.letrify.app.model.Portfolio;
import com.letrify.app.model.Portfolio.PortfolioStatus;
import com.letrify.app.repository.BankRepository;
import com.letrify.app.repository.DiscountRepository;
import com.letrify.app.repository.DocumentDiscountRepository;
import com.letrify.app.repository.DocumentRepository;
import com.letrify.app.repository.ExchangeRateRepository;
import com.letrify.app.repository.PortfolioRepository;
import com.letrify.app.repository.DiscountFeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.RoundingMode;

@Service
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final PortfolioRepository portfolioRepository;
    private final BankRepository bankRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final DiscountFeeRepository discountFeeRepository;
    private final DocumentRepository documentRepository;
    private final DocumentDiscountRepository documentDiscountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository,
                        PortfolioRepository portfolioRepository,
                        BankRepository bankRepository,
                        ExchangeRateRepository exchangeRateRepository,
                        DiscountFeeRepository discountFeeRepository,
                        DocumentRepository documentRepository,
                        DocumentDiscountRepository documentDiscountRepository) {
        this.discountRepository = discountRepository;
        this.portfolioRepository = portfolioRepository;
        this.bankRepository = bankRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.discountFeeRepository = discountFeeRepository;
        this.documentRepository = documentRepository;
        this.documentDiscountRepository = documentDiscountRepository;
    }

    // Crear un nuevo descuento
    public Discount createDiscount(Discount discount, Long portfolioId, Long bankId, Long exchangeRateId, List<DiscountFee> fees) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio no encontrado."));
        
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado."));
        
        discount.setPortfolio(portfolio);
        discount.setBank(bank);
    
        if(exchangeRateId != null){
            ExchangeRate exchangeRate = exchangeRateRepository.findById(exchangeRateId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de cambio no encontrado."));
            discount.setExchangeRate(exchangeRate);
        }
    
        if (discountRepository.existsByPortfolio_IdAndBank_Id(portfolioId, bankId)) {
            throw new IllegalArgumentException("Ya existe un descuento registrado para esta cartera y banco.");
        }

        // 📌 Asignaciones temporales para evitar errores (luego implementarás el cálculo real):
        discount.setInterestAmount(BigDecimal.ZERO);
        discount.setTotalDiscountAmount(BigDecimal.ZERO);
        discount.setTcea(BigDecimal.ZERO);

        Discount savedDiscount = discountRepository.save(discount);

        // Corregimos la conversión de costos porcentuales antes de guardarlos
        for (DiscountFee fee : fees) {
            fee.setDiscount(savedDiscount); // Asociar al descuento recién creado

            if (fee.getFeeType() == DiscountFee.FeeType.PORCENTUAL || fee.getFeeType() == DiscountFee.FeeType.RETENCION) {
                // 🔹 Asegurar que feeAmount esté en formato decimal (ej. 0.002 en vez de 0.2)
                fee.setFeeAmount(fee.getFeeAmount().divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP));
            }

            discountFeeRepository.save(fee);
        }
    
        // 🔹 **Actualizar el estado del portfolio** basado en la fecha del descuento:
        if (discount.getDiscountDate().isAfter(LocalDate.now())) {
            portfolio.setStatus(PortfolioStatus.PROGRAMADO);
        } else {
            portfolio.setStatus(PortfolioStatus.EN_DESCUENTO);
        }

        // Asociar el banco al portfolio
        portfolio.setBank(bank);
        portfolioRepository.save(portfolio); // Guardar cambios en el portfolio

        return savedDiscount;
    }

    // Método para asociar documentos a un descuento
    public void applyDiscountToDocuments(Long discountId, Long portfolioId, BigDecimal discountRate) {
        System.out.println("Método applyDiscountToDocuments() ha sido llamado con:");
        System.out.println("  ➤ discountId: " + discountId);
        System.out.println("  ➤ portfolioId: " + portfolioId);
        System.out.println("  ➤ discountRate: " + discountRate);

        // 1️⃣ Obtener el descuento
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new IllegalArgumentException("Descuento no encontrado."));

        // 2️⃣ Obtener los documentos pendientes de la cartera
        List<Document> documents = documentRepository.findByPortfolio_IdAndStatusAndDiscountDateIsNull(portfolioId, Document.DocumentStatus.PENDIENTE);

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("No hay documentos pendientes en esta cartera.");
        }

        List<DocumentDiscount> documentDiscounts = new ArrayList<>();

        // 3️⃣ Calcular `discountDays` antes de hacer cualquier operación
        for (Document document : documents) {
            System.out.println("📌 Documento ID: " + document.getId());
            System.out.println("➡️ Fecha de descuento (discountDate): " + discount.getDiscountDate());
            System.out.println("➡️ Fecha de vencimiento (dueDate): " + document.getDueDate());
        
            int discountDays = (int) ChronoUnit.DAYS.between(discount.getDiscountDate(), document.getDueDate());
        
            System.out.println("📢 Días de descuento calculados (discountDays): " + discountDays);
        
            if (discountDays <= 0) {
                System.out.println("❌ ERROR: discountDays es <= 0, esto es incorrecto.");
            }
        
            document.setDiscountDays(discountDays);
        }

        // GUARDAR los documentos actualizados en la base de datos ANTES de usarlos
        documentRepository.saveAll(documents);

        // 4️⃣ Calcular `discount_rate`, `interestAmount`, `netValue` y crear la relación en `document_discounts`
        for (Document document : documents) {
            System.out.println("📌 Documento ID: " + document.getId() + " | Discount Days (BD actualizado y recargado): " + document.getDiscountDays());

            DocumentDiscount documentDiscount = new DocumentDiscount();
            documentDiscount.setDiscount(discount);
            documentDiscount.setDocument(document);
            documentDiscount.setNominalValue(document.getAmount()); // Monto original del documento

            // Calcular discount_rate con `discountDays`
            BigDecimal discountRateCalculated = calculateDiscountRate(discount, document.getDiscountDays());
            documentDiscount.setDiscountRate(discountRateCalculated);
            
            // Calcular el interés descontado
            BigDecimal interestAmount = document.getAmount().multiply(discountRateCalculated).setScale(2, RoundingMode.HALF_UP);
            documentDiscount.setInterestAmount(interestAmount);

            // Calcular el valor neto después del descuento
            BigDecimal netValue = document.getAmount().multiply(BigDecimal.ONE.subtract(discountRateCalculated))
                                        .setScale(2, RoundingMode.HALF_UP);
            documentDiscount.setNetValue(netValue);

            // 5️⃣ Obtener los costos iniciales asociados al descuento
            List<DiscountFee> initialFees = discountFeeRepository.findByDiscount_IdAndFeeTiming(discountId, DiscountFee.FeeTiming.INICIAL);
            BigDecimal totalInitialCosts = BigDecimal.ZERO;

            for (DiscountFee fee : initialFees) {
                if (fee.getFeeType() == DiscountFee.FeeType.FIJO) {
                    // ➤ Sumar costos fijos directamente
                    totalInitialCosts = totalInitialCosts.add(fee.getFeeAmount());
                } else if (fee.getFeeType() == DiscountFee.FeeType.PORCENTUAL || fee.getFeeType() == DiscountFee.FeeType.RETENCION) {
                    // ➤ Calcular costos porcentuales sobre el NominalValue
                    BigDecimal percentageCost = documentDiscount.getNominalValue()
                            .multiply(fee.getFeeAmount()) // `feeAmount` ya es decimal (ej. 0.002 para 0.2%)
                            .setScale(2, RoundingMode.HALF_UP);
                    totalInitialCosts = totalInitialCosts.add(percentageCost);
                }
            }

            // 6️⃣ Calcular `receivedValue`
            BigDecimal receivedValue = netValue.subtract(totalInitialCosts).setScale(2, RoundingMode.HALF_UP);
            documentDiscount.setReceivedValue(receivedValue);

            // 7️⃣ Obtener los costos finales asociados al descuento
            List<DiscountFee> finalFees = discountFeeRepository.findByDiscount_IdAndFeeTiming(discountId, DiscountFee.FeeTiming.FINAL);
            BigDecimal totalFinalCosts = BigDecimal.ZERO;

            for (DiscountFee fee : finalFees) {
                if (fee.getFeeType() == DiscountFee.FeeType.FIJO) {
                    // ➤ Sumar costos fijos directamente
                    totalFinalCosts = totalFinalCosts.add(fee.getFeeAmount());
                } else if (fee.getFeeType() == DiscountFee.FeeType.PORCENTUAL) {
                    // ➤ Calcular costos porcentuales sobre el NominalValue
                    BigDecimal percentageCost = documentDiscount.getNominalValue()
                            .multiply(fee.getFeeAmount()) // `feeAmount` ya es decimal (ej. 0.002 para 0.2%)
                            .setScale(2, RoundingMode.HALF_UP);
                    totalFinalCosts = totalFinalCosts.add(percentageCost);
                }
            }

            // 8️⃣ Obtener el monto de la retención si existe
            BigDecimal retentionAmount = BigDecimal.ZERO;
            List<DiscountFee> retentionFees = discountFeeRepository.findByDiscount_IdAndFeeType(discountId, DiscountFee.FeeType.RETENCION);
            for (DiscountFee fee : retentionFees) {
                retentionAmount = retentionAmount.add(documentDiscount.getNominalValue().multiply(fee.getFeeAmount())
                        .setScale(2, RoundingMode.HALF_UP));
            }

            // 9️⃣ Calcular `deliveredValue`
            BigDecimal deliveredValue = documentDiscount.getNominalValue().add(totalFinalCosts).subtract(retentionAmount)
                    .setScale(2, RoundingMode.HALF_UP);
            documentDiscount.setDeliveredValue(deliveredValue);

            // 🔟 Calcular la TCEA
            int discountDays = (int) ChronoUnit.DAYS.between(discount.getDiscountDate(), document.getDueDate());
            document.setDiscountDays(discountDays); // Reasignamos el valor actualizado
            
            System.out.println("📢 Recalculando discountDays para TCEA...");
            System.out.println("➡️ discountDays recalculado: " + discountDays);
            System.out.println("➡️ deliveredValue: " + deliveredValue);
            System.out.println("➡️ receivedValue: " + receivedValue);
            
            if (discountDays <= 0) {
                System.out.println("❌ ERROR: discountDays es inválido, no se puede calcular TCEA.");
                documentDiscount.setTcea(BigDecimal.ZERO);
            } else if (receivedValue.compareTo(BigDecimal.ZERO) == 0) {
                System.out.println("❌ ERROR: receivedValue es 0, evitando división por 0.");
                documentDiscount.setTcea(BigDecimal.ZERO);
            } else {
                BigDecimal fraction = deliveredValue.divide(receivedValue, 9, RoundingMode.HALF_UP);
                BigDecimal exponent = BigDecimal.valueOf(360.0 / discountDays);
            
                System.out.println("➡️ fraction (deliveredValue / receivedValue): " + fraction);
                System.out.println("➡️ exponent (360 / discountDays): " + exponent);
            
                BigDecimal tcea = BigDecimal.valueOf(Math.pow(fraction.doubleValue(), exponent.doubleValue()) - 1)
                        .setScale(9, RoundingMode.HALF_UP);
            
                System.out.println("📢 TCEA Calculada: " + tcea);
                documentDiscount.setTcea(tcea);
            }

            documentDiscounts.add(documentDiscount);
        }

        // 1️⃣1️⃣ Guardar las relaciones en la base de datos
        documentDiscountRepository.saveAll(documentDiscounts);

        // 1️⃣2️⃣ Finalmente, actualizar el estado y `discountDate` de los documentos en la BD
        updateDocumentStatus(discount.getId(), portfolioId, discount.getDiscountDate());
        System.out.println("📢 Estados de documentos actualizados después de guardar descuentos.");
    }

    // Actualizar un descuento existente
    public Optional<Discount> updateDiscount(Long id, Discount updatedDiscount) {
        return discountRepository.findById(id).map(discount -> {
            discount.setInterestAmount(updatedDiscount.getInterestAmount());
            discount.setTcea(updatedDiscount.getTcea());
            discount.setTotalDiscountAmount(updatedDiscount.getTotalDiscountAmount());
            discount.setDiscountDate(updatedDiscount.getDiscountDate());
            discount.setRate(updatedDiscount.getRate());
            discount.setRateDays(updatedDiscount.getRateDays());
            discount.setCapitalizationDays(updatedDiscount.getCapitalizationDays());
    
            // No permitir cambiar el tipo de cambio después de la creación
            // discount.setExchangeRate(updatedDiscount.getExchangeRate()); <-- Eliminado
    
            return discountRepository.save(discount);
        });
    }     

    public void updateDocumentStatus(Long discountId, Long portfolioId, LocalDate discountDate) {
        System.out.println("📢 Método updateDocumentStatus() ha sido llamado.");

        // 1️⃣ Obtener los documentos de la cartera afectados
        List<Document> documents = documentRepository.findByPortfolio_IdAndStatus(portfolioId, Document.DocumentStatus.PENDIENTE);
    
        System.out.println("📌 Documentos encontrados para actualizar estado: " + documents.size());

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("No hay documentos pendientes para actualizar su estado.");
        }
    
        // 2️⃣ Recorrer los documentos y actualizar su estado
        for (Document document : documents) {
            document.setDiscountDate(discountDate); // Asignar la fecha de descuento

            if (discountDate.isAfter(LocalDate.now())) {
                document.setStatus(Document.DocumentStatus.PROGRAMADO); // Descuento en el futuro
            } else {
                document.setStatus(Document.DocumentStatus.EN_DESCUENTO); // Descuento inmediato
            }

            // Recalcular los días de descuento para asegurarnos de que estén bien calculados
            int discountDays = (int) ChronoUnit.DAYS.between(discountDate, document.getDueDate());
            document.setDiscountDays(discountDays);
            
            System.out.println("📌 Documento actualizado: ID " + document.getId() +
                            " | Estado: " + document.getStatus() +
                            " | Discount Date: " + document.getDiscountDate() +
                            " | Discount Days: " + document.getDiscountDays());
        }
    
        // 3️⃣ Guardar los cambios en la base de datos
        documentRepository.saveAll(documents);
    }

    // Calcular la tasa de descuento basada en la tasa ingresada
    private BigDecimal calculateDiscountRate(Discount discount, Integer discountDays) {
        if (discountDays == null || discountDays <= 0) {
            throw new IllegalArgumentException("❌ Los días de descuento deben ser mayores a 0.");
        }
    
        BigDecimal rate = discount.getRate(); // Tasa ingresada en formato decimal
        int rateDays = discount.getRateDays(); // Días de la tasa aplicada
        Integer capitalizationDays = discount.getCapitalizationDays(); // Días de capitalización (solo para tasas nominales)
    
        System.out.println("📌 Tasa ingresada (debería estar en decimal, ej. 0.303 para 30.3%): " + rate);
        System.out.println("📌 Días de la tasa ingresada: " + rateDays);
        System.out.println("📌 Días de capitalización: " + capitalizationDays);
        System.out.println("📌 Días de descuento del documento: " + discountDays);
    
        BigDecimal effectiveRate;
    
        if (discount.getRateType() == Discount.RateType.NOMINAL) {
            // 🔹 Convertir tasa nominal a efectiva
            if (capitalizationDays == null || capitalizationDays == 0) {
                throw new IllegalArgumentException("❌ Los días de capitalización no pueden ser nulos o cero para tasas nominales.");
            }
    
            // m = días tasa nominal / días capitalización
            BigDecimal m = BigDecimal.valueOf(rateDays)
                    .divide(BigDecimal.valueOf(capitalizationDays), 9, RoundingMode.HALF_UP);
            
            // n = días de descuento / días capitalización
            BigDecimal n = BigDecimal.valueOf(discountDays)
                    .divide(BigDecimal.valueOf(capitalizationDays), 9, RoundingMode.HALF_UP);
    
            System.out.println("📌 m (rateDays/capitalizationDays): " + m);
            System.out.println("📌 n (discountDays/capitalizationDays): " + n);
    
            // Fórmula: TEP = (1 + TN / m) ^ n - 1
            BigDecimal base = BigDecimal.ONE.add(rate.divide(m, 9, RoundingMode.HALF_UP));
    
            System.out.println("📌 Base antes de la potencia: " + base);
    
            effectiveRate = BigDecimal.valueOf(Math.pow(base.doubleValue(), n.doubleValue())).subtract(BigDecimal.ONE);
    
        } else {
            // 🔹 Convertir tasa efectiva a la del período `discountDays`
            BigDecimal n1 = BigDecimal.valueOf(rateDays);
            BigDecimal n2 = BigDecimal.valueOf(discountDays);
    
            System.out.println("📌 n1 (días de la tasa efectiva original): " + n1);
            System.out.println("📌 n2 (días de la tasa efectiva buscada): " + n2);
    
            // Fórmula: TEP2 = (1 + TEP1)^(n2 / n1) - 1
            BigDecimal base = BigDecimal.ONE.add(rate);
    
            System.out.println("📌 Base antes de la potencia (tasa efectiva): " + base);
    
            effectiveRate = BigDecimal.valueOf(Math.pow(base.doubleValue(), n2.divide(n1, 9, RoundingMode.HALF_UP).doubleValue())).subtract(BigDecimal.ONE);
        }
    
        // 🔹 Convertir tasa efectiva a tasa descontada
        BigDecimal discountRate = effectiveRate.divide(BigDecimal.ONE.add(effectiveRate), 9, RoundingMode.HALF_UP);
    
        System.out.println("📢 Tasa efectiva calculada: " + effectiveRate);
        System.out.println("📢 Tasa descontada calculada (discountRate): " + discountRate);
    
        return discountRate;
    }

    // Eliminar un descuento solo si existe
    public boolean deleteDiscount(Long id) {
        if (discountRepository.existsById(id)) {
            discountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener todos los descuentos
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    // Obtener un descuento por su ID
    public Optional<Discount> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    // Obtener todos los descuentos de una cartera específica
    public List<Discount> getDiscountsByPortfolioId(Long portfolioId) {
        return discountRepository.findByPortfolio_Id(portfolioId);
    }

    // Obtener todos los descuentos de un banco específico
    public List<Discount> getDiscountsByBankId(Long bankId) {
        return discountRepository.findByBank_Id(bankId);
    }

    // Obtener descuentos con una TCEA superior a un valor dado
    public List<Discount> getDiscountsByTceaGreaterThan(BigDecimal tcea) {
        return discountRepository.findByTceaGreaterThan(tcea);
    }

    // Obtener descuentos dentro de un rango de fechas
    public List<Discount> getDiscountsByDateRange(LocalDate startDate, LocalDate endDate) {
        return discountRepository.findByDiscountDateBetween(startDate, endDate);
    }

    public List<DocumentDiscount> getDocumentsByDiscountId(Long discountId) {
        return documentDiscountRepository.findByDiscount_Id(discountId);
    }    

}
