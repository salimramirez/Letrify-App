document.addEventListener("DOMContentLoaded", function () {
    const empresaBtn = document.getElementById("empresaBtn");
    const personaBtn = document.getElementById("personaBtn");
    const empresaFields = document.getElementById("empresaFields");
    const personaFields = document.getElementById("personaFields");

    const empresaImage = document.querySelector(".register-image-empresa");
    const personaImage = document.querySelector(".register-image-persona");

    function toggleActive(buttonToActivate, buttonToDeactivate, sectionToShow, sectionToHide, imageToShow, imageToHide) {
        buttonToActivate.classList.add("btn-toggle-active");
        buttonToActivate.classList.remove("btn-outline-primary", "btn-outline-secondary");

        buttonToDeactivate.classList.remove("btn-toggle-active");
        buttonToDeactivate.classList.add("btn-outline-secondary");

        sectionToShow.classList.remove("d-none");
        sectionToHide.classList.add("d-none");

        // Asegurar que solo una imagen sea visible a la vez
        imageToHide.classList.remove("active");
        imageToShow.classList.add("active");
    }

    // Mostrar empresas por defecto al cargar la página 
    empresaImage.classList.add("active");

    // Pre-cargar la imagen de "Personas" en segundo plano para evitar retraso en el primer cambio
    const preloadImage = new Image();
    preloadImage.src = "/img/register-individuals-image.webp";

    empresaBtn.addEventListener("click", function () {
        toggleActive(empresaBtn, personaBtn, empresaFields, personaFields, empresaImage, personaImage);
    });

    personaBtn.addEventListener("click", function () {
        toggleActive(personaBtn, empresaBtn, personaFields, empresaFields, personaImage, empresaImage);
    });
});
