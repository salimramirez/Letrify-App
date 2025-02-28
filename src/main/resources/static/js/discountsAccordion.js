document.addEventListener("DOMContentLoaded", async () => {
    const accordionContainer = document.getElementById("discountsAccordion");

    async function loadDiscounts() {
        try {
            const response = await fetch("/api/discounts/with-documents");
            const discounts = await response.json();

            accordionContainer.innerHTML = ""; // Limpiar antes de cargar

            discounts.forEach((discount, index) => {
                const portfolio = discount.portfolio;
                const documents = discount.documents || [];

                const item = `
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="heading${index}">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                                📅 ${discount.discountDate} - 💰 ${discount.totalDiscountAmount} - 🏷 ${portfolio.status}
                            </button>
                        </h2>
                        <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#discountsAccordion">
                            <div class="accordion-body">
                                <p><strong>Nombre de Cartera:</strong> ${portfolio.portfolioName}</p>
                                <p><strong>TCEA del Descuento:</strong> ${discount.tcea ? (discount.tcea * 100).toFixed(2) + "%" : "No disponible"}</p>
                                <p><strong>Banco:</strong> ${discount.bank}</p>
                                
                                <h5 class="mt-3">📄 Documentos Asociados</h5>
                                <div class="documents-container">
                                    ${documents.length > 0 ? generateDocumentsView(documents) : "<p>No hay documentos.</p>"}
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                accordionContainer.innerHTML += item;
            });
        } catch (error) {
            console.error("Error cargando los descuentos:", error);
        }
    }

    // 📌 Función para generar la vista de documentos (tabla o lista según el tamaño de pantalla)
    function generateDocumentsView(documents) {
        return window.innerWidth < 768 ? generateDocumentsList(documents) : generateDocumentsTable(documents);
    }

    // 📌 Función para generar la tabla de documentos
    function generateDocumentsTable(documents) {
        let table = `
            <table class="table table-bordered mt-2">
                <thead>
                    <tr>
                        <th>N° Documento</th>
                        <th>Tipo</th>
                        <th>Cliente</th>
                        <th>Valor Nominal</th>
                        <th>Intereses</th>
                        <th>Valor Recibido</th>
                        <th>Valor Entregado</th>
                        <th>TCEA</th>
                    </tr>
                </thead>
                <tbody>`;

        documents.forEach(doc => {
            table += `
                <tr>
                    <td>${doc.documentNumber}</td>
                    <td>${doc.documentType}</td>
                    <td>${doc.customer}</td>
                    <td>S/ ${doc.nominalValue.toFixed(2)}</td>
                    <td>S/ ${doc.interestAmount.toFixed(2)}</td>
                    <td>S/ ${doc.receivedValue.toFixed(2)}</td>
                    <td>S/ ${doc.deliveredValue.toFixed(2)}</td>
                    <td>${doc.tcea ? (doc.tcea * 100).toFixed(2) + "%" : "N/A"}</td>
                </tr>`;
        });

        table += `</tbody></table>`;
        return table;
    }

    // 📌 Función para generar la lista de documentos (vista móvil)
    function generateDocumentsList(documents) {
        let list = `<ul class="documents-list">`;

        documents.forEach(doc => {
            list += `
                <li class="document-item">
                    <strong>${doc.documentNumber} (${doc.documentType})</strong><br>
                    Cliente: ${doc.customer} <br>
                    Valor Nominal: S/ ${doc.nominalValue.toFixed(2)} <br>
                    Intereses: S/ ${doc.interestAmount.toFixed(2)} <br>
                    Valor Recibido: S/ ${doc.receivedValue.toFixed(2)} <br>
                    Valor Entregado: S/ ${doc.deliveredValue.toFixed(2)} <br>
                    TCEA: ${doc.tcea ? (doc.tcea * 100).toFixed(2) + "%" : "N/A"}
                </li>`;
        });

        list += `</ul>`;
        return list;
    }

    // 📌 Evento para detectar cambios en la pantalla y actualizar la vista si es necesario
    window.addEventListener("resize", () => {
        document.querySelectorAll(".documents-container").forEach(container => {
            const docsData = container.getAttribute("data-docs");
            if (!docsData) return; // Si no hay datos, salir de la función

            const documents = JSON.parse(docsData);
            if (!Array.isArray(documents) || documents.length === 0) return; // Verifica que sea un array válido

            // 📌 Actualizar la vista con la versión correcta (tabla o lista)
            container.innerHTML = generateDocumentsView(documents);
        });
    });

    // Cargar descuentos al iniciar
    loadDiscounts();
});
