// discountModalHandler.js

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("addDiscountForm");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        // Captura claramente los datos desde el formulario
        const discountData = {
            portfolioId: document.getElementById("portfolioId").value,
            bankId: document.getElementById("bankId").value,
            discountDate: document.getElementById("discountDiscountDate").value,
            rateType: document.getElementById("rateType").value,
            rateDays: parseInt(document.getElementById("rateDays").value),
            rate: parseFloat(document.getElementById("rate").value),
            capitalizationDays: document.getElementById("capitalizationDays").value 
                                ? parseInt(document.getElementById("capitalizationDays").value)
                                : null,
            exchangeRateId: document.getElementById("exchangeRateId").value 
                            ? parseInt(document.getElementById("exchangeRateId").value)
                            : null,
            fees: [...capturarCostosEscritorio(), ...capturarCostosMovil()] // Combina ambos conjuntos claramente
        };

        // Mostrar claramente en consola los datos capturados para verificar
        console.log("Datos capturados del formulario:", discountData);
    });
});

// Captura claramente los costos desde la tabla de costos bancarios/manuales
function capturarCostosEscritorio() {
    const fees = [];
    const tablaCostos = document.querySelectorAll("#bankFeesTableBody tr");

    tablaCostos.forEach(row => {
        const celdas = row.querySelectorAll("td");

        if (celdas.length === 5) {
            const bankFeeId = row.getAttribute("data-bank-fee-id");

            fees.push({
                bankFeeId: bankFeeId ? parseInt(bankFeeId) : null,
                feeName: celdas[0].textContent.trim(),
                feeType: celdas[1].textContent.trim().toUpperCase() === "FIJO" ? "FIJO" : "PORCENTUAL",
                feeAmount: parseFloat(celdas[2].textContent.replace('%', '').trim()),
                feeTiming: celdas[3].textContent.trim().toUpperCase() === "INICIAL" ? "INICIAL" : "FINAL",
                feeSource: celdas[4].textContent.trim().toUpperCase() === "BANCO" ? "BANCARIO" : "MANUAL"
            });
        }
    });

    return fees;
}

// Función claramente definida para capturar costos desde móviles
function capturarCostosMovil() {
    const fees = [];
    const listaCostos = document.querySelectorAll("#bankFeesListBody .border");

    listaCostos.forEach(item => {
        const nombreCompleto = item.querySelector("p.mb-0").textContent.trim();

        // Asegurar solo costos manuales:
        if (!nombreCompleto.includes("(Otro)")) return;

        const detalles = item.querySelectorAll("p small");

        const bankFeeId = item.getAttribute("data-bank-fee-id"); // siempre será null en manuales, pero se mantiene por consistencia
        const nombre = nombreCompleto.replace(" (Otro)", "").trim();
        const tipo = detalles[0].textContent.replace("Tipo:", "").trim().toUpperCase() === "FIJO" ? "FIJO" : "PORCENTUAL";
        const montoTexto = detalles[1].textContent.replace("Monto:", "").replace("%","").trim();
        const monto = parseFloat(montoTexto);
        const aplicacion = detalles[2].textContent.replace("Aplicación:", "").trim().toUpperCase() === "INICIAL" ? "INICIAL" : "FINAL";

        fees.push({
            bankFeeId: bankFeeId ? parseInt(bankFeeId) : null,
            feeName: nombre,
            feeType: tipo,
            feeAmount: monto,
            feeTiming: aplicacion,
            feeSource: "MANUAL"
        });
    });

    return fees;
}
