// Función para mostrar el mensaje de confirmación o error con animaciones
function mostrarMensaje(mensaje, tipo) {
    const mensajeDiv = document.createElement("div");
    mensajeDiv.classList.add("alert");
    mensajeDiv.classList.add(tipo === "success" ? "alert-success" : "alert-danger");
    mensajeDiv.classList.add("fixed-bottom", "m-3", "mx-auto", "text-center", "alert-dismissible", "fade", "show");
    mensajeDiv.role = "alert";

    // Aplicamos clases para el ancho dinámico en móviles y escritorio
    mensajeDiv.classList.add("mensaje-alerta"); 

    // Animación de entrada (aparece deslizándose desde abajo)
    mensajeDiv.style.opacity = "0";
    mensajeDiv.style.transform = "translateY(20px)";
    mensajeDiv.style.transition = "opacity 0.5s ease-out, transform 0.5s ease-out";

    // Contenido del mensaje con diseño mejorado
    mensajeDiv.innerHTML = `
        <div class="d-flex align-items-center justify-content-between px-3">
            <strong>${mensaje}</strong>
            <button type="button" class="btn-close" aria-label="Close"></button>
        </div>
    `;

    document.body.appendChild(mensajeDiv);

    // Retraso para activar la animación de entrada
    setTimeout(() => {
        mensajeDiv.style.opacity = "1";
        mensajeDiv.style.transform = "translateY(0)";
    }, 50);

    // Animación de salida después de 3 segundos
    setTimeout(() => cerrarMensajeConAnimacion(mensajeDiv), 3000);

    // Evento para animar salida si se cierra con la "X"
    mensajeDiv.querySelector(".btn-close").addEventListener("click", function () {
        cerrarMensajeConAnimacion(mensajeDiv);
    });
}

// Función para animar la salida y eliminar el mensaje
function cerrarMensajeConAnimacion(mensajeDiv) {
    mensajeDiv.style.opacity = "0";
    mensajeDiv.style.transform = "translateY(20px)";
    setTimeout(() => {
        mensajeDiv.remove();
    }, 500); // Espera la duración de la animación antes de eliminarlo
}
