document.addEventListener("DOMContentLoaded", () => {
    const togglePassword = document.querySelector("#togglePassword");
    const passwordField = document.querySelector("#password");
    const icon = document.querySelector("#iconPassword");

    if (togglePassword && passwordField && icon) {
        togglePassword.addEventListener("click", function () {
            const isPassword = passwordField.getAttribute("type") === "password";
            
            // Alternar el tipo de input
            passwordField.setAttribute("type", isPassword ? "text" : "password");
            
            // Animar el ícono con una transición suave
            icon.style.transition = "opacity 0.3s ease, transform 0.3s ease";
            icon.style.opacity = "0.1";
            setTimeout(() => {
                icon.classList.toggle("bi-eye");
                icon.classList.toggle("bi-eye-slash");
                icon.style.opacity = "1";
                icon.style.transform = isPassword ? "rotate(180deg)" : "rotate(0deg)";
            }, 150);
        });
    }

    if (window.location.search.includes("error")) {
        window.history.replaceState({}, document.title, window.location.pathname);
    }
});