document.addEventListener("DOMContentLoaded", function () {
    const carteraForm = document.getElementById("registerCarteraForm");

    if (carteraForm) {
        carteraForm.addEventListener("submit", async function (event) {
            event.preventDefault(); // Evita el envío tradicional

            // Capturar los valores del formulario
            const carteraData = {
                portfolioName: document.getElementById("carteraName").value.trim(),
                description: document.getElementById("carteraDescription").value.trim(),
                discountDate: document.getElementById("discountDate").value,
                currency: document.getElementById("carteraCurrency").value,
                status: "PENDIENTE", // Estado inicial por defecto
                bank: null
            };            

            console.log("📌 Datos enviados al backend:", carteraData);

            try {
                console.log("📌 Datos enviados al backend:", JSON.stringify(carteraData, null, 2));
                const response = await fetch("/api/portfolios", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(carteraData)
                });

                if (!response.ok) {
                    throw new Error("Error al registrar la cartera.");
                }

                const newCartera = await response.json();
                console.log("✅ Cartera registrada con éxito:", newCartera);

                mostrarMensaje("Cartera registrada correctamente.", "success");

                // Cerrar el modal después del registro
                const carteraModal = document.getElementById("registerCarteraModal");
                const modalInstance = bootstrap.Modal.getInstance(carteraModal);
                if (modalInstance) {
                    modalInstance.hide();
                }

                // Limpiar el formulario
                carteraForm.reset();

                // Actualizar la lista de carteras en la UI
                actualizarTablaCarteras();       

            } catch (error) {
                console.error("❌ Error al registrar la cartera:", error);
                mostrarMensaje("Error al registrar la cartera. Inténtalo de nuevo.", "error");
            }
        });
    }
});

// Función para actualizar la tabla de carteras después de registrar una
async function actualizarTablaCarteras() {
    try {
        const response = await fetch("/api/portfolios");
        if (!response.ok) {
            throw new Error("Error al obtener la lista de carteras.");
        }

        const carteras = await response.json();
        console.log("📌 Carteras obtenidas después del registro:", carteras);

        // Insertar carteras en la tabla/cards
        insertarCarterasEnTabla(carteras);
        insertarCarterasEnCards(carteras);

    } catch (error) {
        console.error("❌ Error al actualizar la tabla de carteras:", error);
    }
}

// Función de ejemplo para actualizar la tabla (debes implementarla en otro archivo)
function insertarCarterasEnTabla(carteras) {
    console.log("⚠️ insertarCarterasEnTabla() aún no implementada.");
}

// Función de ejemplo para actualizar las cards en móvil (debes implementarla en otro archivo)
function insertarCarterasEnCards(carteras) {
    console.log("⚠️ insertarCarterasEnCards() aún no implementada.");
}
