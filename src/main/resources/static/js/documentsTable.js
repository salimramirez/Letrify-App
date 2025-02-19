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
    console.log("insertarDocumentosEnTabla() ejecutado", documentos.length, "documentos.");
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
                <button class="btn btn-sm btn-warning edit-btn" data-id="${doc.id}" data-bs-toggle="modal" data-bs-target="#editDocumentModal">Editar</button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${doc.id}" data-number="${doc.documentNumber}" data-bs-toggle="modal" data-bs-target="#confirmDeleteModal">Eliminar</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Añadir evento a los botones de editar
    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", function () {
            const documentId = this.getAttribute("data-id");
            cargarDatosEnModal(documentId);
        });
    });

    // Añadir evento a los botones de eliminar
    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", function () {
            const documentId = this.getAttribute("data-id");
            const documentNumber = this.getAttribute("data-number");

            document.getElementById("deleteDocumentNumber").textContent = documentNumber;
            document.getElementById("confirmDeleteButton").setAttribute("data-id", documentId);
        });
    });

    // Evitar acumulación de eventos en el botón "Guardar Cambios"
    const oldSaveButton = document.querySelector("#saveEditButton");
    const newSaveButton = oldSaveButton.cloneNode(true); // Clonamos el botón para limpiar eventos previos
    oldSaveButton.replaceWith(newSaveButton); // Reemplazamos el botón original

    newSaveButton.addEventListener("click", function (event) {
        event.preventDefault(); // Evita que el formulario recargue la página

        console.log("✅ Botón 'Guardar Cambios' clickeado");

        const form = document.getElementById("editDocumentForm");
        const documentId = form.querySelector('input[name="documentId"]').value; // Obtener el ID del documento

        console.log("📌 ID del documento a actualizar:", documentId);

        actualizarDocumento(documentId, form); // Llamamos la función de actualización
    });

    // Evitar acumulación de eventos en el botón "Confirmar eliminación"
    const oldConfirmButton = document.querySelector("#confirmDeleteButton");
    const newConfirmButton = oldConfirmButton.cloneNode(true); // Clonamos el botón para limpiar eventos previos
    oldConfirmButton.replaceWith(newConfirmButton); // Reemplazamos el botón original por el clonado

    newConfirmButton.addEventListener("click", function () {
        const documentId = this.getAttribute("data-id");
        eliminarDocumento(documentId);
    });

}

// Función para cargar los datos del documento en el modal de edición
function cargarDatosEnModal(documentId) {
    fetch(`/api/documents/${documentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener los datos del documento.");
            }
            return response.json();
        })
        .then(doc => {
            console.log("Documento a editar:", doc);

            const form = document.getElementById("editDocumentForm");

            form.querySelector('input[name="documentId"]').value = doc.id;
            form.querySelector('input[name="customer"]').value = doc.customer;
            form.querySelector('select[name="documentType"]').value = doc.documentType;
            form.querySelector('input[name="documentNumber"]').value = doc.documentNumber;
            form.querySelector('input[name="amount"]').value = doc.amount;
            form.querySelector('select[name="currency"]').value = doc.currency;
            form.querySelector('input[name="issueDate"]').value = doc.issueDate;
            form.querySelector('input[name="dueDate"]').value = doc.dueDate;
            form.querySelector('textarea[name="description"]').value = doc.description;
        })
        .catch(error => {
            console.error("Error al cargar datos en el modal de edición:", error);
        });
}

function actualizarDocumento(id, form) {
    const formData = {
        customer: form.querySelector('input[name="customer"]').value,
        documentType: form.querySelector('select[name="documentType"]').value,
        documentNumber: form.querySelector('input[name="documentNumber"]').value,
        amount: parseFloat(form.querySelector('input[name="amount"]').value),
        currency: form.querySelector('select[name="currency"]').value,
        issueDate: form.querySelector('input[name="issueDate"]').value,
        dueDate: form.querySelector('input[name="dueDate"]').value,
        description: form.querySelector('textarea[name="description"]').value
    };

    fetch(`/api/documents/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Error al actualizar el documento.");
        }
        return response.json();
    })
    .then(updatedDocument => {
        console.log("✅ Documento actualizado con éxito:", updatedDocument);
        mostrarMensaje("Documento actualizado correctamente", "success");
        
        // Cerrar el modal después de la actualización
        const editModal = document.getElementById("editDocumentModal");
        const modalInstance = bootstrap.Modal.getInstance(editModal);
        if (modalInstance) {
            modalInstance.hide();
        }

        // Actualizar la tabla
        return fetch("/api/documents/user");
    })
    .then(response => response.json())
    .then(updatedDocuments => {
        insertarDocumentosEnTabla(updatedDocuments);
    })
    .catch(error => {
        console.error("Error al actualizar el documento:", error);
        mostrarMensaje("Error al actualizar el documento", "error");
    });
}

// Función para eliminar un documento
function eliminarDocumento(id) {
    fetch(`/api/documents/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al eliminar el documento");
            }
            return fetch("/api/documents/user"); // Obtener la lista actualizada después de eliminar
        })
        .then(response => response.json())
        .then(updatedDocuments => {
            insertarDocumentosEnTabla(updatedDocuments); // Actualizar la tabla correctamente
            mostrarMensaje("Documento eliminado con éxito", "success");

            // Cerrar el modal después de la eliminación exitosa
            const deleteModal = document.getElementById("confirmDeleteModal");
            const modalInstance = bootstrap.Modal.getInstance(deleteModal);
            if (modalInstance) {
                modalInstance.hide();
            }
        })
        .catch(error => {
            console.error("Error al eliminar el documento:", error);
            mostrarMensaje("Error al eliminar el documento", "error");
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
