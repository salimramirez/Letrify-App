document.addEventListener("DOMContentLoaded", function () {
    console.log("📌 Cargando carteras...");

    fetch("/api/portfolios")
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener las carteras.");
            }
            return response.json();
        })
        .then(data => {
            console.log("✅ Carteras obtenidas:", data);
            insertarCarterasEnTabla(data);  // Actualizar la tabla en vista escritorio
            insertarCarterasEnCards(data);  // Actualizar las cards en vista móvil
        })
        .catch(error => console.error("❌ Error:", error));
});

// Función para insertar carteras en la tabla
function insertarCarterasEnTabla(carteras) {
    console.log("📌 insertando carteras en tabla", carteras.length, "carteras.");
    const tbody = document.querySelector("#portfolioTableBody");
    tbody.innerHTML = ""; // Limpiar contenido previo

    if (carteras.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center text-muted">No hay carteras registradas.</td></tr>`;
        return;
    }

    carteras.forEach((cartera, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${cartera.portfolioName}</td>
            <td>${formatearFecha(cartera.createdAt)}</td>
            <td>${formatearFecha(cartera.discountDate)}</td>
            <td>${cartera.currency}</td>
            <td class="${getEstadoClase(cartera.status)}">${cartera.status}</td>
            <td>
                <button class="btn btn-sm btn-primary add-doc-btn" data-id="${cartera.id}">Agregar</button>
                <button class="btn btn-sm btn-info view-doc-btn" data-id="${cartera.id}">Ver</button>
            </td>
            <td><span class="badge bg-secondary">${cartera.portfolioDocuments ? cartera.portfolioDocuments.length : 0}</span></td>
            <td>
                <button class="btn btn-sm btn-warning edit-btn" data-id="${cartera.id}">Editar</button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${cartera.id}">Eliminar</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    agregarEventosTabla();
}

// Función para insertar carteras en las cards (vista móvil)
function insertarCarterasEnCards(carteras) {
    console.log("📌 insertando carteras en cards", carteras.length, "carteras.");
    const cardsContainer = document.getElementById("portfolioCardsContainer");
    
    cardsContainer.innerHTML = ""; // Limpiar contenido previo

    if (carteras.length === 0) {
        cardsContainer.innerHTML = `<p class="text-center text-muted">No hay carteras registradas.</p>`;
        return;
    }

    carteras.forEach(cartera => {
        const card = document.createElement("div");
        card.classList.add("card", "mb-3", "border-primary");
        card.innerHTML = `
            <div class="card-body">
                <h5 class="card-title">${cartera.portfolioName}</h5>
                <p class="card-text"><strong>Fecha Creación:</strong> ${formatearFecha(cartera.createdAt)}</p>
                <p class="card-text"><strong>Fecha Descuento:</strong> ${formatearFecha(cartera.discountDate)}</p>
                <p class="card-text"><strong>Moneda:</strong> ${cartera.currency}</p>
                <p class="card-text"><strong>Estado:</strong> <span class="${getEstadoClase(cartera.status)}">${cartera.status}</span></p>
                <p class="card-text"><strong>Documentos:</strong> <span class="badge bg-secondary">${cartera.portfolioDocuments ? cartera.portfolioDocuments.length : 0}</span></p>
                <div class="d-flex justify-content-between">
                    <button class="btn btn-primary btn-sm add-doc-btn" data-id="${cartera.id}">Agregar</button>
                    <button class="btn btn-info btn-sm view-doc-btn" data-id="${cartera.id}">Ver</button>
                </div>
                <div class="d-flex justify-content-between mt-2">
                    <button class="btn btn-warning btn-sm edit-btn" data-id="${cartera.id}">Editar</button>
                    <button class="btn btn-danger btn-sm delete-btn" data-id="${cartera.id}">Eliminar</button>
                </div>
            </div>
        `;
        cardsContainer.appendChild(card);
    });

    agregarEventosCards();
}

// Función para asignar eventos a los botones en la tabla
function agregarEventosTabla() {
    document.querySelectorAll(".add-doc-btn").forEach(button => {
        button.addEventListener("click", function () {
            const portfolioId = this.getAttribute("data-id");
            console.log("📌 Agregar documentos a cartera:", portfolioId);
        });
    });

    document.querySelectorAll(".view-doc-btn").forEach(button => {
        button.addEventListener("click", function () {
            const portfolioId = this.getAttribute("data-id");
            console.log("📌 Ver documentos de cartera:", portfolioId);
        });
    });

    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", function () {
            const portfolioId = this.getAttribute("data-id");
            console.log("📌 Editar cartera:", portfolioId);
        });
    });

    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", function () {
            const portfolioId = this.getAttribute("data-id");
            console.log("📌 Eliminar cartera:", portfolioId);
        });
    });
}

// Función para asignar eventos a los botones en las cards
function agregarEventosCards() {
    agregarEventosTabla(); // Se usa la misma lógica que la tabla
}

// Función para formatear la fecha en formato legible
function formatearFecha(fecha) {
    if (!fecha) return "N/A";

    // Si la fecha contiene una 'T', significa que tiene hora y debemos cortarla
    if (fecha.includes("T")) {
        fecha = fecha.split("T")[0]; // Tomar solo la parte "YYYY-MM-DD"
    }

    // Separar la fecha en año, mes y día
    const [year, month, day] = fecha.split("-");

    return `${day}/${month}/${year}`; // Formato: DD/MM/YYYY
}

// Función para asignar colores a los estados
function getEstadoClase(estado) {
    if (estado === "PENDIENTE") return "text-warning fw-bold";
    if (estado === "EN DESCUENTO") return "text-primary fw-bold";
    if (estado === "CANCELADA") return "text-danger fw-bold";
    return "";
}
