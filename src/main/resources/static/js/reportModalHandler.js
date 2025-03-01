document.addEventListener("DOMContentLoaded", function () {
    const generarReporteBtn = document.getElementById("generarReporteBtn");
    const confirmarReporteBtn = document.getElementById("confirmarReporteBtn");
    const cerrarModalBtn = document.getElementById("cerrarModalBtn");
    const reportModal = new bootstrap.Modal(document.getElementById("reportModal"));

    // Abrir el modal cuando se presiona "Generar Reporte"
    generarReporteBtn.addEventListener("click", function () {
        reportModal.show();
    });

    // Confirmar y generar el reporte
    confirmarReporteBtn.addEventListener("click", function () {
        generarReporte();
    });

    // Función para llamar a la API y generar el reporte
    function generarReporte() {
        fetch("/api/portfolios/with-discounts")
            .then(response => response.json())
            .then(portfolios => {
                console.log("Datos de carteras enviados al backend:", portfolios); // Verificar en consola
    
                // Si portfolios está vacío o es null, lanzar un error
                if (!portfolios || portfolios.length === 0) {
                    throw new Error("No se encontraron carteras para generar el reporte.");
                }
    
                const portfolioSnapshot = JSON.stringify(portfolios);
    
                return fetch("/api/reports/generate", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ portfolioSnapshot }),
                });
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error en la API: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Reporte generado correctamente:", data);
                reportModal.hide();
                actualizarListaReportes();
            })
            .catch(error => {
                console.error("Error al generar el reporte:", error);
                alert("Error al generar el reporte. Revisa la consola para más detalles.");
            });
    }

    // Función para actualizar la lista de reportes después de generar uno nuevo
    function actualizarListaReportes() {
        fetch("/api/reports")
            .then(response => response.json())
            .then(reportes => {
                const reportesContainer = document.getElementById("reportesContainer");
                reportesContainer.innerHTML = ""; // Limpiar contenido previo
                
                reportes.forEach(reporte => {
                    // Extraer solo el nombre del archivo desde reportPdfPath
                    const fileName = reporte.reportPdfPath.split("/").pop();

                    // Convertir la fecha en un identificador amigable
                    const reportDate = new Date(reporte.reportDate);
                    const formattedDate = reportDate.getFullYear() +
                        String(reportDate.getMonth() + 1).padStart(2, '0') +
                        String(reportDate.getDate()).padStart(2, '0') +
                        String(reportDate.getHours()).padStart(2, '0') +
                        String(reportDate.getMinutes()).padStart(2, '0') +
                        String(reportDate.getSeconds()).padStart(2, '0');

                    // Nombre del reporte en formato "Reporte-YYYYMMDDHHMMSS"
                    const reportName = `Reporte-${formattedDate}`;

                    const card = document.createElement("div");
                    card.className = "col-md-4 mb-3";
                    card.innerHTML = `
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">${reportName}</h5>
                                <p class="card-text">Fecha: ${new Date(reporte.reportDate).toLocaleString()}</p>
                                <p class="card-text">Estado: <strong>${reporte.reportStatus}</strong></p>
                                <a href="/api/reports/view/${fileName}" target="_blank" class="btn btn-secondary">Abrir PDF</a>
                            </div>
                        </div>
                    `;
                    reportesContainer.appendChild(card);
                });
            })
            .catch(error => console.error("Error al cargar reportes:", error));
    }

    // Cargar reportes al inicio
    actualizarListaReportes();
});
