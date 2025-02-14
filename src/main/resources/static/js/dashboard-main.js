// Toggle de la sidebar
const wrapper = document.getElementById("wrapper");
const menuToggle = document.getElementById("menu-toggle");
const overlay = document.getElementById("overlay");
const sidebar = document.getElementById("sidebar-wrapper");

menuToggle.addEventListener("click", () => {
    if (window.innerWidth <= 768) {
        wrapper.classList.toggle("toggled");
    } else {
        // Toggle del sidebar en escritorio
        if (sidebar.style.marginLeft === "0px" || sidebar.style.marginLeft === "") {
            sidebar.style.marginLeft = "-250px";
        } else {
            sidebar.style.marginLeft = "0px";
        }
    }
});

overlay.addEventListener("click", () => {
    wrapper.classList.remove("toggled");
});

const links = document.querySelectorAll('.list-group-item');
const sections = document.querySelectorAll('.content-section');

links.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();

        const dropdownContent = link.nextElementSibling;
        if (dropdownContent && dropdownContent.classList.contains('dropdown-content')) {
            const parent = link.parentElement;
            parent.classList.toggle('expanded'); // Aquí activamos la clase para rotar el ícono

            if (parent.classList.contains('expanded')) {
                dropdownContent.style.maxHeight = dropdownContent.scrollHeight + "px";
            } else {
                dropdownContent.style.maxHeight = "0";
            }
            return;
        }

        const target = link.getAttribute('data-target');
        if (target) {
            sections.forEach(section => {
                section.classList.remove('active');
            });
            document.getElementById(target).classList.add('active');
        }
    });
});

document.getElementById('registerDocumentForm').addEventListener('submit', function (e) {
    e.preventDefault();
    alert('Documento registrado con éxito');
    // Aquí puedes agregar la lógica para enviar el formulario al backend.
});