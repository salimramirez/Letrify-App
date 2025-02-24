document.addEventListener("DOMContentLoaded", function () {
    cargarBancos();
    cargarCarteras();
    obtenerTipoCambio(); // Llamar a la función al cargar el formulario

    // Seleccionamos los elementos del formulario
    const rateTypeSelect = document.getElementById("rateType");
    const capitalizationRow = document.getElementById("capitalizationRow");

    // Evento para detectar cambio en el tipo de tasa
    rateTypeSelect.addEventListener("change", function () {
        if (this.value === "NOMINAL") {
            capitalizationRow.style.display = "block"; // Mostrar el campo
        } else {
            capitalizationRow.style.display = "none";  // Ocultar el campo
        }
    });

    // Llamamos una vez la función al cargar la página para inicializar correctamente
    actualizarCapitalizacion();

    // Detectar cuando el usuario selecciona un banco
    document.getElementById("bankId").addEventListener("change", function () {
        let bankId = this.value;
        if (bankId) {
            cargarCostosBanco(bankId);
        }
    });

    // Eventos para mostrar/ocultar formulario de costos manuales (escritorio)
    document.getElementById("btnAddManualCostTable").addEventListener("click", function () {
        toggleManualCostForm("Table");
    });
    document.getElementById("btnCancelManualCostTable").addEventListener("click", function () {
        toggleManualCostForm("Table", false);
    });

    // Eventos para mostrar/ocultar formulario de costos manuales (móviles)
    document.getElementById("btnAddManualCostList").addEventListener("click", function () {
        toggleManualCostForm("List");
    });
    document.getElementById("btnCancelManualCostList").addEventListener("click", function () {
        toggleManualCostForm("List", false);
    });

    // Eventos para guardar costos manuales
    document.getElementById("btnSaveManualCostTable").addEventListener("click", function () {
        agregarCostoManual("Table");
    });
    document.getElementById("btnSaveManualCostList").addEventListener("click", function () {
        agregarCostoManual("List");
    });
});

/**
 * Carga los bancos desde el backend y los inserta en el select de bancos.
 */
async function cargarBancos() {
    try {
        let response = await fetch("/api/banks");
        if (!response.ok) throw new Error("Error al obtener bancos");

        let bancos = await response.json();
        let selectBanco = document.getElementById("bankId");
        selectBanco.innerHTML = '<option value="" selected disabled>Seleccione un Banco</option>';

        bancos.forEach(banco => {
            let option = document.createElement("option");
            option.value = banco.id;
            option.textContent = banco.bankName;
            selectBanco.appendChild(option);
        });

    } catch (error) {
        console.error("❌ Error cargando bancos:", error);
    }
}


/**
 * Carga las carteras desde el backend y las inserta en el select de carteras.
 */
async function cargarCarteras() {
    try {
        let response = await fetch("/api/portfolios");
        if (!response.ok) throw new Error("Error al obtener carteras");

        let carteras = await response.json();
        let selectCartera = document.getElementById("portfolioId");
        selectCartera.innerHTML = '<option value="" selected disabled>Seleccione una Cartera</option>';

        carteras
            .filter(cartera => cartera.documentCount > 0) // Filtrar solo carteras con documentos
            .forEach(cartera => {
                let option = document.createElement("option");
                option.value = cartera.id;
                option.textContent = `${cartera.portfolioName} (${cartera.currency}) - ${cartera.documentCount} documentos`;
                selectCartera.appendChild(option);
            });

        if (selectCartera.children.length === 1) {
            selectCartera.innerHTML = '<option value="" selected disabled>No hay carteras disponibles</option>';
        }

    } catch (error) {
        console.error("❌ Error cargando carteras:", error);
    }
}

/**
 * Carga los costos del banco seleccionado y los inserta en la tabla o lista.
 */
async function cargarCostosBanco(bankId) {
    try {
        let response = await fetch(`/api/bank-fees/bank/${bankId}`);
        if (!response.ok) throw new Error("Error al obtener costos del banco");

        let costos = await response.json();
        let tablaBody = document.getElementById("bankFeesTableBody");
        let listaBody = document.getElementById("bankFeesListBody");
        let contenedorTabla = document.getElementById("bankFeesContainerTable");
        let contenedorLista = document.getElementById("bankFeesContainerList");
        let btnAddManualCostTable = document.getElementById("btnAddManualCostTable");
        let btnAddManualCostList = document.getElementById("btnAddManualCostList");

        tablaBody.innerHTML = "";
        listaBody.innerHTML = "";

        if (costos.length === 0) {
            let mensajeFila = `<tr class="sin-costos"><td colspan="5" class="text-center text-muted">Sin costos bancarios</td></tr>`;
            let mensajeLista = `<div class="sin-costos text-muted text-center">Sin costos bancarios</div>`;
        
            tablaBody.innerHTML = mensajeFila;
            listaBody.innerHTML = mensajeLista;
        
            contenedorTabla.style.display = "block";
            contenedorLista.style.display = "block";
        
            btnAddManualCostTable.style.display = "inline-block";
            btnAddManualCostList.style.display = "inline-block";
        
            return;
        }

        costos.forEach(costo => {
            // **Verificamos si el costo es PORCENTUAL y lo convertimos a porcentaje**
            let formattedAmount = costo.feeType === "PORCENTUAL" 
                ? `${(costo.feeAmount * 100).toFixed(2)}%`  // Multiplicamos por 100 y agregamos "%"
                : costo.feeAmount;  // Si es fijo, lo dejamos igual

            let fila = `<tr data-bank-fee-id="${costo.id}">
                <td>${costo.feeName}</td>
                <td>${costo.feeType === "FIJO" ? "Fijo" : "Porcentual"}</td>
                <td>${formattedAmount}</td>
                <td>${costo.feeTiming === "INICIAL" ? "Inicial" : "Final"}</td>
                <td>Banco</td>
            </tr>`;
            tablaBody.innerHTML += fila;
        });

        costos.forEach(costo => {
            let formattedAmount = costo.feeType === "PORCENTUAL" 
                ? `${(costo.feeAmount * 100).toFixed(2)}%` 
                : costo.feeAmount;

            let item = `<div class="border p-1 mb-1 rounded" style="font-size: 14px;" data-bank-fee-id="${costo.id}">
                <p class="mb-0"><strong>${costo.feeName}</strong> (Banco)</p>
                <p class="mb-0"><small>Tipo: ${costo.feeType === "FIJO" ? "Fijo" : "Porcentual"}</small></p>
                <p class="mb-0"><small>Monto: ${formattedAmount}</small></p>
                <p class="mb-0"><small>Aplicación: ${costo.feeTiming === "INICIAL" ? "Inicial" : "Final"}</small></p>
            </div>`;
            listaBody.innerHTML += item;
        });

        contenedorTabla.style.display = "block";
        contenedorLista.style.display = "block";
        btnAddManualCostTable.style.display = "inline-block";
        btnAddManualCostList.style.display = "inline-block";

    } catch (error) {
        console.error("❌ Error cargando costos del banco:", error);
    }
}

/**
 * Consulta el tipo de cambio en tiempo real desde el backend (API SUNAT).
 */
async function obtenerTipoCambio() {
    try {
        let response = await fetch("/api/exchange-rates");
        if (!response.ok) throw new Error("Error al obtener el tipo de cambio");

        let exchangeRate = await response.json();

        // Mostrar solo el tipo de cambio de compra (buyRate)
        document.getElementById("exchangeRate").textContent = exchangeRate.buyRate;
        document.getElementById("exchangeDate").textContent = `${formatearFecha(exchangeRate.exchangeDate)}`;
        document.getElementById("source").textContent = `${exchangeRate.source}`;
        document.getElementById("lastUpdated").textContent = `${formatearFechaHora(exchangeRate.lastUpdated)}`;

        // NUEVO: Guardar claramente el exchangeRate.id en el campo oculto
        document.getElementById("exchangeRateId").value = exchangeRate.id;

    } catch (error) {
        console.error("❌ Error obteniendo el tipo de cambio:", error);
        document.getElementById("exchangeRate").textContent = "Error";
        document.getElementById("exchangeDate").textContent = "No disponible";
        document.getElementById("source").textContent = "";
        document.getElementById("lastUpdated").textContent = "";
        document.getElementById("exchangeRateId").value = ""; // Limpiar si ocurre error
    }
}


/**
 * Formatea una fecha de "YYYY-MM-DD" a "DD/MM/YYYY".
 */
function formatearFecha(fechaISO) {
    if (!fechaISO) return "No disponible";
    let partes = fechaISO.split("-");
    return `${partes[2]}/${partes[1]}/${partes[0]}`; // Formato DD/MM/YYYY
}

/**
 * Formatea una fecha y hora de "YYYY-MM-DDTHH:mm:ss" a "DD/MM/YYYY HH:mm:ss".
 */
function formatearFechaHora(fechaHoraISO) {
    if (!fechaHoraISO) return "No disponible";
    let fecha = new Date(fechaHoraISO);
    return fecha.toLocaleString("es-ES", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: true // Formato 24 horas
    });
}

/**
 * Muestra u oculta el formulario de costos manuales.
 */
function toggleManualCostForm(type, show = true) {
    let form = document.getElementById(`manualCostForm${type}`);

    if (!show) { // Cuando ocultas, limpia los campos
        document.getElementById(`manualCostName${type}`).value = "";
        document.getElementById(`manualCostAmount${type}`).value = "";
        document.getElementById(`manualCostType${type}`).selectedIndex = 0; // Opcional
        document.getElementById(`manualCostTiming${type}`).selectedIndex = 0; // Opcional
    }

    form.style.display = show ? "block" : "none";
}

/**
 * Agrega un costo manual a la tabla o lista.
 */
function agregarCostoManual(view) {
    let name = document.getElementById(`manualCostName${view}`).value.trim();
    let type = document.getElementById(`manualCostType${view}`).value;
    let amount = document.getElementById(`manualCostAmount${view}`).value.trim();
    let timing = document.getElementById(`manualCostTiming${view}`).value;

    if (!name || !amount) {
        alert("Debe completar todos los campos para agregar un costo.");
        return;
    }

    let formattedType = type === "FIJO" ? "Fijo" : "Porcentual";
    let formattedTiming = timing === "INICIAL" ? "Inicial" : "Final";

    if (view === "Table") {
        let tablaBody = document.getElementById("bankFeesTableBody");

        // **Eliminamos el mensaje "Sin costos bancarios" si existe**
        let mensajeSinCostos = tablaBody.querySelector(".sin-costos");
        if (mensajeSinCostos) mensajeSinCostos.remove();

        let nuevaFila = `<tr>
            <td>${name}</td>
            <td>${formattedType}</td>
            <td>${amount}</td>
            <td>${formattedTiming}</td>
            <td>Otro</td>
        </tr>`;

        tablaBody.innerHTML += nuevaFila;
    } else {
        let listaBody = document.getElementById("bankFeesListBody");

        // **Eliminamos el mensaje "Sin costos bancarios" si existe**
        let mensajeSinCostos = listaBody.querySelector(".sin-costos");
        if (mensajeSinCostos) mensajeSinCostos.remove();

        let nuevoItem = `<div class="border p-1 mb-1 rounded" style="font-size: 14px;">
            <p class="mb-0"><strong>${name}</strong> (Otro)</p>
            <p class="mb-0"><small>Tipo: ${formattedType}</small></p>
            <p class="mb-0"><small>Monto: ${amount}</small></p>
            <p class="mb-0"><small>Aplicación: ${formattedTiming}</small></p>
        </div>`;

        listaBody.innerHTML += nuevoItem;
    }

    // **Limpiar formulario después de agregar**
    document.getElementById(`manualCostName${view}`).value = "";
    document.getElementById(`manualCostAmount${view}`).value = "";

    // **Ocultar el formulario de costos manuales después de agregar**
    toggleManualCostForm(view, false);
}

/**
 * Función para mostrar/ocultar el campo "Período de Capitalización"
 * según el tipo de tasa seleccionado.
 */
function actualizarCapitalizacion() {
    const rateTypeSelect = document.getElementById("rateType");
    const capitalizationRow = document.getElementById("capitalizationRow");

    if (rateTypeSelect.value === "NOMINAL") {
        capitalizationRow.style.display = "block";
    } else {
        capitalizationRow.style.display = "none";
    }
}