document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registerDocumentForm");

    form.addEventListener("submit", async function (event) {
        event.preventDefault(); // Evita que el formulario se envíe de forma clásica.

        try {
            // Obtener los datos del usuario autenticado desde /api/user/me
            const userResponse = await fetch("/api/user/me", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include" // Necesario si usas cookies de sesión
            });

            if (!userResponse.ok) {
                throw new Error("No se pudo obtener la información del usuario.");
            }

            const userData = await userResponse.json();
            console.log("Datos del usuario autenticado:", userData);

            // Capturar los datos del formulario
            const formData = {
                customer: form.querySelector('input[name="customer"]').value,
                documentType: form.querySelector('select[name="documentType"]').value,
                documentNumber: form.querySelector('input[name="documentNumber"]').value,
                amount: parseFloat(form.querySelector('input[name="amount"]').value),
                currency: form.querySelector('select[name="currency"]').value,
                issueDate: form.querySelector('input[name="issueDate"]').value,
                dueDate: form.querySelector('input[name="dueDate"]').value,
                description: form.querySelector('textarea[name="description"]').value,
                status: "PENDIENTE",  // Valor fijo enviado desde el frontend

                // Agregar companyId o individualId según el tipo de usuario
                companyId: userData.userType === "COMPANY" ? userData.companyId : null,
                individualId: userData.userType === "INDIVIDUAL" ? userData.individualId : null
            };

            console.log("Datos que se enviarán al backend:", formData);

            // Enviar los datos al backend usando fetch
            const response = await fetch("/api/documents", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                mostrarMensaje("Documento creado exitosamente.", "success");
                form.reset(); // Limpiar el formulario

                // Aquí se actualizan los documentos sin hacer refresh de la página
                const updatedDocuments = await fetch("/api/documents/user");  // Solicitar la lista actualizada
                const documentsData = await updatedDocuments.json(); // Obtener los datos de los documentos
                console.log("Documentos obtenidos después de enviar el formulario:", documentsData);

                // Actualizar la tabla con los nuevos documentos
                insertarDocumentosEnTabla(documentsData);
                insertarDocumentosEnCards(documentsData);
                
            } else {
                const errorData = await response.json();
                console.error("Error al crear el documento:", errorData);
                mostrarMensaje("Ocurrió un error. Revisa los datos e inténtalo nuevamente.", "error");
            }

        } catch (error) {
            console.error("Error en la solicitud:", error);
            mostrarMensaje("Error de red. Inténtalo nuevamente.", "error");
        }
    });
});
