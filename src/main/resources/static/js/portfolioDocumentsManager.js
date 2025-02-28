document.addEventListener("DOMContentLoaded", function () {
    console.log("📌 portfolioDocumentsManager.js cargado.");

    // 📌 Asignar eventos a los botones de "Agregar Documento"
    asignarEventosAgregarDocumento();

    // 📌 Detectar clic en "Guardar" para asignar documentos a la cartera
    document.getElementById("saveSelectedDocuments").addEventListener("click", async function () {
        const portfolioId = this.getAttribute("data-portfolio-id");
        
        // Obtener los documentos seleccionados (checkboxes marcados que NO están deshabilitados)
        const selectedDocs = Array.from(document.querySelectorAll("#documentList input:checked"))
            .filter(input => !input.disabled)  // Filtrar solo los habilitados
            .map(input => input.value);

        console.log("📌 Documentos seleccionados para la cartera (excluyendo deshabilitados):", selectedDocs);
    
        try {
            // Obtener los documentos que ya estaban asignados antes de abrir el modal
            const documentosAsignados = await obtenerDocumentosAsignados(portfolioId);
            console.log("📌 Documentos asignados antes de la actualización:", documentosAsignados);
    
            // 🚀 Documentos a AGREGAR: están en selectedDocs pero NO en documentosAsignados
            const documentosParaAgregar = selectedDocs.filter(docId => !documentosAsignados.includes(parseInt(docId)));
    
            // 🚀 Documentos a ELIMINAR: están en documentosAsignados pero NO en selectedDocs
            const documentosParaEliminar = documentosAsignados.filter(docId => !selectedDocs.includes(docId.toString()));
    
            console.log("📌 Documentos a agregar:", documentosParaAgregar);
            console.log("📌 Documentos a eliminar:", documentosParaEliminar);
    
            // 🔹 Agregar nuevos documentos (POST)
            for (let docId of documentosParaAgregar) {
                const response = await fetch(`/api/portfolios/${portfolioId}/documents/${docId}`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" }
                });
    
                if (!response.ok) throw new Error(`Error al asignar documento ${docId}.`);
            }
    
            // 🔹 Eliminar documentos desmarcados (DELETE)
            for (let docId of documentosParaEliminar) {
                const response = await fetch(`/api/portfolios/${portfolioId}/documents/${docId}`, {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json" }
                });
    
                if (!response.ok) throw new Error(`Error al eliminar documento ${docId}.`);
            }
    
            console.log("✅ Documentos actualizados correctamente.");
            mostrarMensaje("Los cambios en la cartera han sido guardados.", "success");
    
            // Cerrar el modal
            const modalInstance = bootstrap.Modal.getInstance(document.getElementById("selectDocumentsModal"));
            if (modalInstance) modalInstance.hide();
    
            // Actualizar la UI de carteras
            actualizarTablaCarteras();

            // Actualizar la lista de carteras en el select del formulario de descuento
            if (typeof cargarCarteras === "function") {
                console.log("🔄 Actualizando lista de carteras en el formulario de descuentos...");
                cargarCarteras(); // Llama a la función ya existente en discountFormHandler.js
            } else {
                console.warn("⚠️ cargarCarteras() no está definida en este contexto.");
            }         
    
        } catch (error) {
            console.error("❌ Error al actualizar documentos de la cartera:", error);
            alert("Error al guardar los cambios. Inténtalo de nuevo.");
        }
    });
});

// 📌 Función para asignar eventos a los botones "Agregar Documento"
function asignarEventosAgregarDocumento() {
    document.querySelectorAll(".add-doc-btn").forEach(button => {
        button.removeEventListener("click", abrirModalSeleccionDocumentos); // Evita eventos duplicados
        button.addEventListener("click", abrirModalSeleccionDocumentos);
    });
}

// 📌 Función que abre el modal de selección de documentos
function abrirModalSeleccionDocumentos() {
    const portfolioId = this.getAttribute("data-id");
    console.log("📌 Selección de documentos para cartera:", portfolioId);

    // 📌 Seleccionar SOLO la fila de la cartera en la tabla correcta
    const portfolioRow = document.querySelector(`#portfolioTableBody button[data-id="${portfolioId}"]`)?.closest("tr");

    if (!portfolioRow) {
        console.error("❌ No se encontró la fila de la cartera correcta.");
        return;
    }

    console.log("🔍 Fila seleccionada:", portfolioRow ? portfolioRow.innerHTML : "No encontrada");

    // 📌 CORRECCIÓN: Buscar las celdas de la cartera por clases CSS en lugar de índices fijos
    const portfolioName = portfolioRow.querySelector(".portfolio-name")?.textContent.trim() || "Desconocido";
    const portfolioCurrency = portfolioRow.querySelector(".portfolio-currency")?.textContent.trim() || "N/A";   

    console.log("📌 Nombre detectado:", portfolioName);
    console.log("📌 Moneda detectada:", portfolioCurrency);

    // Asignar los valores en el modal
    document.getElementById("selectedPortfolioName").textContent = portfolioName;
    document.getElementById("selectedPortfolioCurrency").textContent = portfolioCurrency;

    // Guardar el ID de la cartera en el botón de guardar
    document.getElementById("saveSelectedDocuments").setAttribute("data-portfolio-id", portfolioId);
    
    // Abrir el modal
    const modal = new bootstrap.Modal(document.getElementById("selectDocumentsModal"));
    modal.show();

    console.log(`📌 Llamando a cargarDocumentosDisponibles con: portfolioId=${portfolioId}, portfolioCurrency=${portfolioCurrency}`);    

    // Cargar los documentos disponibles en el modal
    cargarDocumentosDisponibles(portfolioId, portfolioCurrency);

}

// Función para cargar documentos disponibles en el modal
async function cargarDocumentosDisponibles(portfolioId, portfolioCurrency) {
    const documentList = document.getElementById("documentList");
    documentList.innerHTML = "<p class='text-muted'>Cargando documentos...</p>";

    try {
        const response = await fetch("/api/documents");
        if (!response.ok) throw new Error("Error al obtener documentos.");

        const documentos = await response.json();
        console.log("✅ Documentos disponibles:", documentos);

        // Aquí llamamos a obtenerDocumentosAsignados() con portfolioId correctamente
        const documentosAsignados = await obtenerDocumentosAsignados(portfolioId);
        console.log("📌 Documentos ya asignados a la cartera:", documentosAsignados);

        // Obtener los documentos asignados a otras carteras
        const documentosDeOtrasCarteras = await obtenerDocumentosDeOtrasCarteras(portfolioId);

        documentList.innerHTML = ""; // Limpiar antes de cargar

        if (documentos.length === 0) {
            documentList.innerHTML = "<p class='text-muted'>No hay documentos disponibles.</p>";
            return;
        }

        console.log("📌 Documentos antes del filtrado:", documentos);
        console.log("📌 Moneda de la cartera seleccionada:", portfolioCurrency);
        
        // 📌 Validar que la moneda esté bien definida
        if (!portfolioCurrency || portfolioCurrency === "N/A") {
            console.error("❌ Error: La moneda de la cartera no está definida correctamente.");
            return;
        }

        // Obtener documentos filtrados según la moneda de la cartera
        const documentosFiltrados = obtenerDocumentosFiltrados(documentos, portfolioCurrency);
        console.log("📌 Documentos después del filtrado por moneda:", documentosFiltrados);

        if (documentosFiltrados.length === 0) {
            console.warn(`⚠️ No hay documentos disponibles en ${portfolioCurrency}. 
                          Documentos totales: ${documentos.length}, 
                          Documentos filtrados: ${documentosFiltrados.length}`);
            documentList.innerHTML = "<p class='text-muted'>No hay documentos disponibles con esta moneda.</p>";
            return;
        }

        // Variable para verificar si hay documentos deshabilitados
        let hayDocumentosDeshabilitados = false;

        // Agregar input de búsqueda en vivo
        documentList.appendChild(crearBuscadorDocumentos());

        // Contenedor para mostrar la tabla o cards
        const container = document.createElement("div");
        container.id = "documentContainer";
        documentList.appendChild(container);

        if (window.innerWidth > 768) {
            // Mostrar tabla en escritorio
            container.appendChild(crearTablaDocumentos(documentosFiltrados, documentosAsignados, documentosDeOtrasCarteras));

            // Recorremos los documentos en modo tabla para ver si alguno está deshabilitado
            documentosFiltrados.forEach(doc => {
                if (documentosDeOtrasCarteras.includes(doc.id) && !documentosAsignados.includes(doc.id)) {
                    hayDocumentosDeshabilitados = true;
                }
            });
        } else {
            // Mostrar cards en móviles
            documentosFiltrados.forEach(doc => {
                const isChecked = documentosAsignados.includes(doc.id) || documentosDeOtrasCarteras.includes(doc.id) ? "checked" : "";
                const isDisabled = documentosDeOtrasCarteras.includes(doc.id) && !documentosAsignados.includes(doc.id) ? "disabled" : "";
                if (isDisabled) hayDocumentosDeshabilitados = true;
                container.appendChild(crearCardDocumento(doc, isChecked, isDisabled));
            });
        }

        // Mostrar mensaje si hay documentos deshabilitados
        if (hayDocumentosDeshabilitados) {
            mostrarMensajeDocumentosDeshabilitados(documentList);
        }

        activarBuscadorDocumentos();

    } catch (error) {
        console.error("❌ Error al cargar documentos:", error);
        documentList.innerHTML = "<p class='text-danger'>Error al cargar documentos.</p>";
    }
}

async function obtenerDocumentosAsignados(portfolioId) {
    try {
        const response = await fetch(`/api/portfolios/${portfolioId}/documents`);
        if (!response.ok) throw new Error("Error al obtener documentos asignados.");
        
        const documentosAsignados = await response.json();
        
        return documentosAsignados.map(doc => doc.id); // Solo necesitamos los IDs
    } catch (error) {
        console.error("❌ Error al obtener documentos asignados:", error);
        return [];
    }
}

// Función para obtener los documentos asignados a otras carteras
async function obtenerDocumentosDeOtrasCarteras(portfolioId) {
    try {
        // Cambiar la URL para que coincida con el endpoint correcto en el backend
        const response = await fetch(`/api/portfolios/other-portfolios/${portfolioId}/documents`);
        if (!response.ok) throw new Error("Error al obtener documentos de otras carteras.");

        const documentosDeOtrasCarteras = await response.json();
        console.log("📌 Documentos de otras carteras:", documentosDeOtrasCarteras);

        return documentosDeOtrasCarteras.map(doc => doc.id); // Obtener solo los IDs de los documentos
    } catch (error) {
        console.error("❌ Error al obtener documentos de otras carteras:", error);
        return [];
    }
}

// 📌 Función para formatear fechas (DD/MM/YYYY)
function formatearFecha(fecha) {
    if (!fecha) return "N/A";
    if (fecha.includes("T")) fecha = fecha.split("T")[0]; // Remover hora si la tiene
    const [year, month, day] = fecha.split("-");
    return `${day}/${month}/${year}`;
}

// Función para obtener documentos filtrados por la moneda de la cartera
function obtenerDocumentosFiltrados(documentos, portfolioCurrency) {
    return documentos.filter(doc => doc.currency === portfolioCurrency);
}

// Función para crear el input de búsqueda en vivo con icono de lupa y botón de "X"
function crearBuscadorDocumentos() {
    const searchContainer = document.createElement("div");
    searchContainer.classList.add("input-group", "mb-3"); // Utiliza Bootstrap Input Group

    // Icono de lupa
    const searchIcon = document.createElement("span");
    searchIcon.classList.add("input-group-text"); 
    searchIcon.innerHTML = `<i class="bi bi-search"></i>`; 

    // Input de búsqueda
    const searchInput = document.createElement("input");
    searchInput.type = "text";
    searchInput.id = "documentSearch";
    searchInput.classList.add("form-control");
    searchInput.placeholder = "Buscar documento...";

    // Botón de "X" para limpiar el campo
    const clearButton = document.createElement("button");
    clearButton.type = "button";
    clearButton.classList.add("btn", "btn-outline-secondary", "d-none"); // Oculto inicialmente
    clearButton.innerHTML = `<i class="bi bi-x-circle"></i>`;

    // Evento para limpiar el input al hacer clic en "X"
    clearButton.addEventListener("click", function () {
        searchInput.value = "";
        searchInput.dispatchEvent(new Event("input")); // Simular evento input para refrescar la búsqueda
        clearButton.classList.add("d-none"); // Ocultar el botón nuevamente
    });

    // Evento para mostrar/ocultar botón "X"
    searchInput.addEventListener("input", function () {
        clearButton.classList.toggle("d-none", searchInput.value === "");
    });

    // Agregar elementos al contenedor
    searchContainer.appendChild(searchIcon);
    searchContainer.appendChild(searchInput);
    searchContainer.appendChild(clearButton);

    return searchContainer;
}

// Función para activar la búsqueda en vivo
function activarBuscadorDocumentos() {
    const searchInput = document.getElementById("documentSearch");
    searchInput.addEventListener("input", function () {
        const searchText = searchInput.value.toLowerCase();
        document.querySelectorAll("#documentContainer .document-item").forEach(item => {
            const text = item.textContent.toLowerCase();
            item.style.display = text.includes(searchText) ? "" : "none";
        });
    });
}

// Función para crear la tabla de documentos
function crearTablaDocumentos(documentos, documentosAsignados, documentosDeOtrasCarteras) {
    // Contenedor con scroll interno
    const tableContainer = document.createElement("div");
    tableContainer.id = "documentTableContainer";
    tableContainer.classList.add("table-responsive");

    const table = document.createElement("table");
    table.classList.add("table", "table-striped", "table-hover");

    // Encabezado de la tabla
    table.innerHTML = `
        <thead>
            <tr>
                <th>✔</th>
                <th>Número</th>
                <th>Monto</th>
                <th>Moneda</th>
                <th>Vencimiento</th>
            </tr>
        </thead>
        <tbody id="documentTableBody"></tbody>
    `;

    const tbody = table.querySelector("#documentTableBody");

    documentos.forEach(doc => {
        const isChecked = documentosAsignados.includes(doc.id) || documentosDeOtrasCarteras.includes(doc.id) ? "checked" : "";
        const isDisabled = documentosDeOtrasCarteras.includes(doc.id) && !documentosAsignados.includes(doc.id) ? "disabled" : "";

        const row = document.createElement("tr");
        row.classList.add("document-item"); // Para el buscador

        if (isDisabled) row.classList.add("bg-light", "text-muted"); // Fondo gris si está deshabilitado

        row.innerHTML = `
            <td><input type="checkbox" class="form-check-input" value="${doc.id}" ${isChecked} ${isDisabled} style="${isDisabled ? 'opacity: 0.5;' : ''}"></td>
            <td>${doc.documentNumber}</td>
            <td>${doc.amount}</td>
            <td>${doc.currency}</td>
            <td>${formatearFecha(doc.dueDate)}</td>
        `;
        tbody.appendChild(row);
    });

    // Agregar la tabla dentro del contenedor
    tableContainer.appendChild(table);
    return tableContainer;
}

// Función para crear un card de documento (modo móvil)
function crearCardDocumento(doc, isChecked, isDisabled) {
    const card = document.createElement("div");
    card.classList.add("card", "mb-2", "document-item");

    if (isDisabled) card.classList.add("bg-light", "text-muted"); // Fondo gris si está deshabilitado

    card.innerHTML = `
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="card-title">${doc.documentNumber}</h5>
                <input type="checkbox" class="form-check-input" value="${doc.id}" ${isChecked} ${isDisabled} style="${isDisabled ? 'opacity: 0.5;' : ''}">
            </div>
            <p class="card-text"><strong>Monto:</strong> ${doc.amount} ${doc.currency}</p>
            <p class="card-text"><strong>Vence:</strong> ${formatearFecha(doc.dueDate)}</p>
        </div>
    `;

    return card;
}

// Función para crear el HTML de un documento
// function crearElementoDocumento(doc, isChecked, isDisabled) {
//     const docItem = document.createElement("div");
//     docItem.classList.add("list-group-item");
//     if (isDisabled) docItem.classList.add("bg-light", "text-muted"); // Estilo visual

//     docItem.innerHTML = `
//         <input type="checkbox" class="form-check-input me-2" value="${doc.id}" id="doc-${doc.id}" ${isChecked} ${isDisabled} style="${isDisabled ? 'opacity: 0.5;' : ''}">
//         <label for="doc-${doc.id}">${doc.documentNumber} - ${doc.amount} ${doc.currency} (Vence: ${formatearFecha(doc.dueDate)}) 
//         ${isDisabled ? "<span class='text-danger ms-2'>(X)</span>" : ""}</label>
//     `;

//     return docItem;
// }

// Función para mostrar mensaje de documentos deshabilitados
function mostrarMensajeDocumentosDeshabilitados(container) {
    const warningMessage = document.createElement("p");
    warningMessage.classList.add("text-muted", "fst-italic", "mb-3");
    warningMessage.innerHTML = `
        <i class="bi bi-exclamation-triangle-fill text-warning"> </i>
        Algunos documentos están deshabilitados porque ya pertenecen a otra cartera.
    `;
    container.appendChild(warningMessage);
}