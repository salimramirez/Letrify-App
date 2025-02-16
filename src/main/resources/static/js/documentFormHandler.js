document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registerDocumentForm");

    form.addEventListener("submit", function (event) {
        event.preventDefault(); // Evita que el formulario se envíe de forma clásica.

        // Capturamos los datos del formulario
        const formData = {
            customer: form.querySelector('input[name="customer"]').value,
            documentType: form.querySelector('select[name="documentType"]').value,
            documentNumber: form.querySelector('input[name="documentNumber"]').value,
            amount: parseFloat(form.querySelector('input[name="amount"]').value),
            currency: form.querySelector('select[name="currency"]').value,
            issueDate: form.querySelector('input[name="issueDate"]').value,
            dueDate: form.querySelector('input[name="dueDate"]').value,
            description: form.querySelector('textarea[name="description"]').value,
            status: "PENDIENTE"  // Valor fijo enviado desde el frontend
        };

        console.log("Datos capturados:", formData);
        console.log("Valor de customer:", formData.customer);

        // Enviamos los datos como JSON al backend usando fetch
        fetch("/api/documents", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (response.ok) {
                alert("Documento creado exitosamente.");
                form.reset(); // Limpiar el formulario
            } else {
                return response.json().then(errorData => {
                    console.error("Error al crear el documento:", errorData);
                    alert("Ocurrió un error. Revisa los datos e inténtalo nuevamente.");
                });
            }
        })
        .catch(error => {
            console.error("Error en la solicitud:", error);
            alert("Error de red. Inténtalo nuevamente.");
        });
    });
});
