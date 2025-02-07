document.addEventListener('DOMContentLoaded', function () {
    
    // ==========================
    // CONFIGURACIÓN INICIAL AL CARGAR LA PÁGINA
    // ==========================

    const empresaBtn = document.getElementById('empresaBtn');
    const personaBtn = document.getElementById('personaBtn');
    const userTypeInput = document.getElementById('userTypeInput');

    // Evento para el botón de Empresas
    empresaBtn.addEventListener('click', () => {
        empresaBtn.classList.add('active', 'btn-primary');
        personaBtn.classList.remove('active', 'btn-primary');
        personaBtn.classList.add('btn-outline-secondary');

        userTypeInput.value = 'COMPANY'; // Actualiza el valor del campo oculto
    });

    // Evento para el botón de Personas
    personaBtn.addEventListener('click', () => {
        personaBtn.classList.add('active', 'btn-primary');
        empresaBtn.classList.remove('active', 'btn-primary');
        empresaBtn.classList.add('btn-outline-secondary');

        userTypeInput.value = 'INDIVIDUAL'; // Actualiza el valor del campo oculto
    });

    // Asegurar que al cargar la página los campos ocultos estén deshabilitados
    toggleFieldset('personaFields', true);

    // Verifica si hay un valor en el input oculto
    if (userTypeInput.value === 'INDIVIDUAL') {
        document.getElementById('personaBtn').click(); // Activa la pestaña Personas
    } else {
        document.getElementById('empresaBtn').click(); // Activa la pestaña Empresas
    }

    // ==========================
    // MANEJO DE SECCIONES EMPRESAS/PERSONAS
    // ==========================

    // Alternar entre Empresas y Personas y Deshabilitar Campos Ocultos
    document.getElementById('empresaBtn').addEventListener('click', function() {
        document.getElementById('empresaFields').classList.remove('d-none');
        document.getElementById('personaFields').classList.add('d-none');
        this.classList.add('active');
        document.getElementById('personaBtn').classList.remove('active');

        toggleFieldset('empresaFields', false); // Habilitar los campos de empresa
        toggleFieldset('personaFields', true);  // Deshabilitar los campos de persona
    });

    document.getElementById('personaBtn').addEventListener('click', function() {
        document.getElementById('personaFields').classList.remove('d-none');
        document.getElementById('empresaFields').classList.add('d-none');
        this.classList.add('active');
        document.getElementById('empresaBtn').classList.remove('active');

        toggleFieldset('personaFields', false); // Habilitar los campos de persona
        toggleFieldset('empresaFields', true);  // Deshabilitar los campos de empresa
    });

    // Funcíon para habilitar o deshabilitar campos ocultos
    function toggleFieldset(fieldsetId, disable) {
        const fields = document.querySelectorAll(`#${fieldsetId} input`);
        fields.forEach(field => {
            field.disabled = disable;
        });
    }

    // ==========================
    // VALIDACIONES DE CAMPOS
    // ==========================

    // Validación del RUC (Empresas)
    document.getElementById('ruc').addEventListener('input', function () {
        const rucInput = this; // Referencia al input
        const rucPattern = /^\d{11}$/; // Solo 11 dígitos exactos
        const isValid = rucPattern.test(rucInput.value);

        // Manejar tooltip de error con fade-in/out
        const errorTooltip = document.getElementById('rucError');
        errorTooltip.classList.toggle('visible', !isValid && rucInput.value.trim() !== "");

        // Agregar o quitar borde rojo en el input
        this.classList.toggle('is-valid', isValid);
        this.classList.toggle('is-invalid', !isValid && this.value.trim() !== "");
    });

    // document.getElementById('ruc').addEventListener('input', function () {
    //     const rucInput = this; // Referencia al input
    //     const rucPattern = /^\d{11}$/; // Solo 11 dígitos exactos
    //     const isValid = rucPattern.test(rucInput.value);
    
    //     // Aplicar clases de Bootstrap
    //     this.classList.toggle('is-valid', isValid);
    //     this.classList.toggle('is-invalid', !isValid && this.value.trim() !== "");
    
    //     // Mostrar/ocultar el mensaje de error manualmente
    //     const errorTooltip = document.getElementById('rucError');
    //     if (!isValid && rucInput.value.trim() !== "") {
    //         errorTooltip.style.display = "block"; // Mostrar mensaje
    //     } else {
    //         errorTooltip.style.display = "none"; // Ocultar mensaje
    //     }
    // });

    // Validación del correo electrónico (Empresas)
    document.getElementById('emailEmpresa').addEventListener('input', function () {
        const emailInput = this; // Referencia al input
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Expresión regular para validar correos
        const isValid = emailPattern.test(emailInput.value);
    
        // Obtener el tooltip de error
        const errorTooltip = document.getElementById('emailEmpresaError');
    
        // Manejar tooltip de error con fade-in/out
        errorTooltip.classList.toggle('visible', !isValid && emailInput.value.trim() !== "");
    
        // Aplicar clases de Bootstrap para validación
        emailInput.classList.toggle('is-valid', isValid);
        emailInput.classList.toggle('is-invalid', !isValid && emailInput.value.trim() !== "");
    });

    // Validación del Número de Teléfono (Empresas)
    document.getElementById('phoneEmpresa').addEventListener('input', function () {
        const phoneInput = this; // Referencia al input
        const phonePattern = /^\d{9}$/; // Solo 9 dígitos exactos
        const countryCodePattern = /^\+\d+/; // Detecta si hay un código de país (ej: +51, +34)
    
        const errorTooltip = document.getElementById('phoneEmpresaError');
    
        if (countryCodePattern.test(phoneInput.value)) {
            // Si el usuario ingresa un código de país, mostrar mensaje especial
            errorTooltip.textContent = "No hace falta código de país.";
            errorTooltip.classList.add('visible');
            this.classList.add('is-invalid');
            this.classList.remove('is-valid');
        } else {
            // Validar solo si son 9 dígitos
            const isValid = phonePattern.test(phoneInput.value);
            errorTooltip.textContent = "Debe tener 9 dígitos."; // Mensaje normal
    
            // Mostrar u ocultar el tooltip según la validez
            errorTooltip.classList.toggle('visible', !isValid && phoneInput.value.trim() !== "");
    
            // Aplicar clases de Bootstrap para borde verde/rojo
            this.classList.toggle('is-valid', isValid);
            this.classList.toggle('is-invalid', !isValid && phoneInput.value.trim() !== "");
        }
    });

    // Validación de la Dirección Fiscal
    document.getElementById('fiscal_address').addEventListener('input', function () {
        const addressInput = this; // Referencia al input
        const minLength = 5; // Longitud mínima requerida
        const isValid = addressInput.value.trim().length >= minLength;

        // Manejar tooltip de error con fade-in/out
        const errorTooltip = document.getElementById('fiscalAddressError');
        errorTooltip.classList.toggle('visible', !isValid && addressInput.value.trim() !== "");

        // Aplicar clases de Bootstrap para el borde de validación
        this.classList.toggle('is-valid', isValid);
        this.classList.toggle('is-invalid', !isValid && this.value.trim() !== "");
    });

    // ###
    // Validación del sector
    // ###

    // Seleccionar elementos
    const industrySelect = document.getElementById('industry'); // Select de sectores
    const otherIndustryContainer = document.getElementById('otherIndustryContainer'); // Contenedor del input "Otro"
    const otherIndustryInput = document.getElementById('otherIndustry'); // Input de "Otro"

    // Agregar evento de cambio al selector
    industrySelect.addEventListener('change', function () {
        if (this.value === 'Otro') {
            otherIndustryContainer.classList.remove('d-none'); // Mostrar input
            otherIndustryContainer.classList.add('d-flex'); // Mantener la estructura
            otherIndustryInput.setAttribute('required', 'true'); // Hacerlo obligatorio

            // No marcar el select como válido o inválido
            this.classList.remove('is-valid', 'is-invalid');
        } else {
            otherIndustryContainer.classList.remove('d-flex'); // Evitar que afecte el diseño
            otherIndustryContainer.classList.add('d-none'); // Ocultar input
            otherIndustryInput.removeAttribute('required'); // Quitar requerimiento
            otherIndustryInput.value = ''; // Limpiar input
            otherIndustryInput.classList.remove('is-valid', 'is-invalid'); // Resetear validación del input

            // Validación del select (Solo si elige algo distinto de "Otro")
            if (this.value === "") { // Si está en "Selecciona un sector"
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            }
        }
    });

    // Validación del input "Otro"
    otherIndustryInput.addEventListener('input', function () {
        if (this.value.trim() === "") {
            this.classList.remove('is-valid');
            if (this.hasAttribute('required')) { // Solo mostrar inválido si es obligatorio
                this.classList.add('is-invalid');
            }
        } else {
            this.classList.remove('is-invalid');
            this.classList.add('is-valid');
        }
    });

    // ###

    // Validación del correo electrónico (Personas)
    document.getElementById('emailPersona').addEventListener('input', function () {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;  // Patrón para correos electrónicos válidos
        validateField(this, 'emailPersonaValidIcon', 'emailPersonaInvalidIcon', emailPattern);
    });

    // Validación de contraseñas (Empresas)
    document.getElementById('passwordEmpresa').addEventListener('input', function () {
        handlePasswordValidation(this, 'passwordStrengthBarEmpresa', 'passwordStrengthTextEmpresa', 'passwordTooltipEmpresa');
    });

    document.getElementById('confirmPasswordEmpresa').addEventListener('input', function () {
        handleConfirmPasswordValidation('passwordEmpresa', 'confirmPasswordEmpresa', 'confirmPasswordValidIconEmpresa', 'confirmPasswordInvalidIconEmpresa');
    });

    // Validación de contraseñas (Personas)
    document.getElementById('passwordPersona').addEventListener('input', function () {
        handlePasswordValidation(this, 'passwordStrengthBarPersona', 'passwordStrengthTextPersona');
    });

    document.getElementById('confirmPasswordPersona').addEventListener('input', function () {
        handleConfirmPasswordValidation('passwordPersona', 'confirmPasswordPersona', 'confirmPasswordValidIconPersona', 'confirmPasswordInvalidIconPersona');
    });

    // ==========================
    // FUNCIONES DE VALIDACIÓN
    // ==========================

    // Función para validar un campo con un patrón y opcionalmente mostrar un mensaje de error
    function validateField(inputElement, validIcon, invalidIcon, errorElement = null, pattern = /.+/) {
        const value = inputElement.value.trim();
        const isValid = pattern.test(value);
    
        // Actualizar íconos de validación
        toggleIcons(validIcon, invalidIcon, isValid);
    
        // Manejar visibilidad del mensaje de error si se proporciona
        if (errorElement) {
            errorElement.classList.toggle('visible', !isValid);
        }
    }

    // Mostrar o Ocultar Íconos según sea válido o no
    function toggleIcons(validIcon, invalidIcon, isValid) {
        validIcon.classList.toggle('d-none', !isValid);
        invalidIcon.classList.toggle('d-none', isValid);
    }

    function handlePasswordValidation(passwordElement, strengthBarId, strengthTextId, tooltipId) {
        const password = passwordElement.value;
        const strengthBar = document.getElementById(strengthBarId);
        const strengthText = document.getElementById(strengthTextId);
        const tooltip = document.getElementById(tooltipId);
    
        if (password === '') {
            strengthBar.style.width = '0%';
            strengthBar.className = 'progress-bar';
            strengthText.textContent = 'Mínimo 6 caracteres';
    
            // Ocultar tooltip
            tooltip.classList.add('d-none');
            tooltip.classList.remove('visible');
    
            // No marcar inválido si está vacío
            passwordElement.classList.remove('is-valid', 'is-invalid');
            return;
        }
    
        // Mostrar el tooltip eliminando d-none
        tooltip.classList.remove('d-none');
        tooltip.classList.add('visible');
    
        // Evaluar la fuerza de la contraseña
        const strength = evaluatePasswordStrength(password);
        strengthBar.style.width = strength.percent + '%';
        strengthBar.className = 'progress-bar ' + strength.colorClass;
        strengthText.textContent = strength.message;
    
        // Validar la contraseña como válida solo si tiene 6+ caracteres y es media o fuerte
        const isValid = password.length >= 6 && (strength.message === 'Contraseña media' || strength.message === 'Contraseña fuerte');
    
        passwordElement.classList.toggle('is-valid', isValid);
        passwordElement.classList.toggle('is-invalid', !isValid);
    }

    function handleConfirmPasswordValidation(passwordId, confirmPasswordId, validIconId, invalidIconId) {
        const passwordValue = document.getElementById(passwordId).value;
        const confirmPasswordValue = document.getElementById(confirmPasswordId).value;
        const isMatch = passwordValue === confirmPasswordValue && confirmPasswordValue !== '';
        toggleIcons(validIconId, invalidIconId, isMatch);
    }

    function evaluatePasswordStrength(password) {
        let strengthScore = 0;

        if (password.length >= 6) strengthScore++;
        if (password.match(/[a-z]/)) strengthScore++;
        if (password.match(/[A-Z]/)) strengthScore++;
        if (password.match(/[0-9]/)) strengthScore++;
        if (password.match(/[^a-zA-Z0-9]/)) strengthScore++;

        if (strengthScore <= 2) {
            return { percent: 25, colorClass: 'bg-danger', message: 'Contraseña débil' };
        } else if (strengthScore === 3) {
            return { percent: 50, colorClass: 'bg-warning', message: 'Contraseña media' };
        } else if (strengthScore >= 4) {
            return { percent: 100, colorClass: 'bg-success', message: 'Contraseña fuerte' };
        }
    }

});