document.addEventListener("DOMContentLoaded", function () {
    console.log("Cargando documentos...");

    fetch("/api/documents/user")
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener los documentos.");
            }
            return response.json();
        })
        .then(data => {
            console.log("Documentos obtenidos:", data);
            insertarDocumentosEnTabla(data);
        })
        .catch(error => console.error("Error:", error));
});

function insertarDocumentosEnTabla(documentos) {
    const tbody = document.querySelector("#gestion-documentos tbody");
    tbody.innerHTML = ""; // Limpiar contenido previo

    if (documentos.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center text-muted">No hay documentos registrados.</td></tr>`;
        return;
    }

    documentos.forEach((doc, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${formatearTexto(doc.documentType)}</td>
            <td>${doc.documentNumber}</td>
            <td>${doc.customer}</td>
            <td>${doc.currency} ${formatearMonto(doc.amount)}</td>
            <td>${formatearFecha(doc.issueDate)}</td>
            <td>${formatearFecha(doc.dueDate)}</td>
            <td class="${getEstadoClase(doc.status)}">${formatearTexto(doc.status)}</td>
            <td>
                <button class="btn btn-sm btn-warning">Editar</button>
                <button class="btn btn-sm btn-danger">Eliminar</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Función para formatear la fecha en formato legible
function formatearFecha(fecha) {
    if (!fecha) return "N/A";
    // Si la fecha ya está en formato YYYY-MM-DD, solo se debe separar año, mes, día
    const date = new Date(fecha + 'T00:00:00'); // Aseguramos que no haya hora al asignar "00:00:00"
    const day = String(date.getUTCDate()).padStart(2, '0');
    const month = String(date.getUTCMonth() + 1).padStart(2, '0'); // Ajuste para mes en JavaScript
    const year = date.getUTCFullYear();
    return `${day}/${month}/${year}`; // Formato: DD/MM/YYYY
}

// Función para formatear montos con separador de miles
function formatearMonto(monto) {
    return new Intl.NumberFormat("es-PE", { minimumFractionDigits: 2 }).format(monto);
}

// Función para mejorar la presentación de texto (Mayúscula inicial)
function formatearTexto(texto) {
    return texto.charAt(0).toUpperCase() + texto.slice(1).toLowerCase();
}

// Función para asignar colores a los estados
function getEstadoClase(estado) {
    if (estado === "PENDIENTE") return "text-warning fw-bold";
    if (estado === "APROBADO") return "text-success fw-bold";
    if (estado === "RECHAZADO") return "text-danger fw-bold";
    return "";
}
