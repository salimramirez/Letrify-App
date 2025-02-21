document.addEventListener("DOMContentLoaded", function () {
    console.log("📌 portfolioDocumentsManager.js cargado.");

    // 📌 Asignar eventos a los botones de "Agregar Documento"
    asignarEventosAgregarDocumento();

    // 📌 Detectar clic en "Guardar" para asignar documentos a la cartera
    document.getElementById("saveSelectedDocuments").addEventListener("click", async function () {
        const portfolioId = this.getAttribute("data-portfolio-id");
    
        // Obtener los documentos seleccionados (checkboxes marcados)
        const selectedDocs = Array.from(document.querySelectorAll("#documentList input:checked")).map(input => input.value);
    
        console.log("📌 Documentos seleccionados para la cartera:", selectedDocs);
    
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
            alert("Los cambios en la cartera han sido guardados.");
    
            // Cerrar el modal
            const modalInstance = bootstrap.Modal.getInstance(document.getElementById("selectDocumentsModal"));
            if (modalInstance) modalInstance.hide();
    
            // Actualizar la UI de carteras
            actualizarTablaCarteras();
    
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

    // Obtener el nombre y la moneda de la cartera desde la UI
    const portfolioRow = document.querySelector(`button[data-id="${portfolioId}"]`).closest("tr");
    const portfolioName = portfolioRow ? portfolioRow.children[1].textContent : "Desconocido";
    const portfolioCurrency = portfolioRow ? portfolioRow.children[4].textContent : "N/A";

    // Asignar los valores en el modal
    document.getElementById("selectedPortfolioName").textContent = portfolioName;
    document.getElementById("selectedPortfolioCurrency").textContent = portfolioCurrency;

    // Guardar el ID de la cartera en el botón de guardar
    document.getElementById("saveSelectedDocuments").setAttribute("data-portfolio-id", portfolioId);
    
    // Abrir el modal
    const modal = new bootstrap.Modal(document.getElementById("selectDocumentsModal"));
    modal.show();

    // Cargar los documentos disponibles en el modal
    cargarDocumentosDisponibles(portfolioId);
}

// 📌 Función para cargar documentos disponibles en el modal
async function cargarDocumentosDisponibles(portfolioId) {
    const documentList = document.getElementById("documentList");
    documentList.innerHTML = "<p class='text-muted'>Cargando documentos...</p>";

    try {
        const response = await fetch("/api/documents");
        if (!response.ok) throw new Error("Error al obtener documentos.");

        const documentos = await response.json();
        console.log("✅ Documentos disponibles:", documentos);

        // 🚀 Aquí llamamos a obtenerDocumentosAsignados() con portfolioId correctamente
        const documentosAsignados = await obtenerDocumentosAsignados(portfolioId);
        console.log("📌 Documentos ya asignados a la cartera:", documentosAsignados);

        documentList.innerHTML = ""; // Limpiar antes de cargar

        if (documentos.length === 0) {
            documentList.innerHTML = "<p class='text-muted'>No hay documentos disponibles.</p>";
            return;
        }

        // Crear checkboxes para seleccionar documentos y marcar los ya asignados
        documentos.forEach(doc => {
            const isChecked = documentosAsignados.includes(doc.id) ? "checked" : ""; // Si está asignado, marcarlo

            const docItem = document.createElement("div");
            docItem.classList.add("list-group-item");
            docItem.innerHTML = `
                <input type="checkbox" class="form-check-input me-2" value="${doc.id}" id="doc-${doc.id}" ${isChecked}>
                <label for="doc-${doc.id}">${doc.documentNumber} - ${doc.amount} ${doc.currency} (Vence: ${formatearFecha(doc.dueDate)})</label>
            `;
            documentList.appendChild(docItem);
        });

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

// 📌 Función para formatear fechas (DD/MM/YYYY)
function formatearFecha(fecha) {
    if (!fecha) return "N/A";
    if (fecha.includes("T")) fecha = fecha.split("T")[0]; // Remover hora si la tiene
    const [year, month, day] = fecha.split("-");
    return `${day}/${month}/${year}`;
}
