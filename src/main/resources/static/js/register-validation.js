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

    // Validación del Razón Social (Empresas)
    document.getElementById('business_name').addEventListener('input', function () {
        const businessInput = this;
        const minLength = 5;
        const isValid = businessInput.value.trim().length >= minLength;
    
        // Manejar tooltip de error
        const errorTooltip = document.getElementById('businessNameError');
        errorTooltip.classList.toggle('visible', !isValid && businessInput.value.trim() !== "");
    
        // Aplicar Bootstrap validation
        businessInput.classList.toggle('is-valid', isValid);
        businessInput.classList.toggle('is-invalid', !isValid && businessInput.value.trim() !== "");
    });
    
    // Ocultar el globo de error cuando el usuario pierde el foco
    document.getElementById('business_name').addEventListener('blur', function () {
        document.getElementById('businessNameError').classList.remove('visible');
    });
    
    // Reaparecer el globo de error si el usuario vuelve al campo
    document.getElementById('business_name').addEventListener('focus', function () {
        if (!this.classList.contains('is-valid') && this.value.trim() !== "") {
            document.getElementById('businessNameError').classList.add('visible');
        }
    });

    // Validación del RUC (Empresas)
    document.getElementById('ruc').addEventListener('input', function () {
        const rucInput = this;
        const rucPattern = /^\d{11}$/;
        const isValid = rucPattern.test(rucInput.value);
    
        // Manejar tooltip de error
        const errorTooltip = document.getElementById('rucError');
        errorTooltip.classList.toggle('visible', !isValid && rucInput.value.trim() !== "");
    
        // Aplicar Bootstrap validation
        this.classList.toggle('is-valid', isValid);
        this.classList.toggle('is-invalid', !isValid && this.value.trim() !== "");
    });
    
    // Ocultar el globo de error cuando el usuario pierde el foco
    document.getElementById('ruc').addEventListener('blur', function () {
        document.getElementById('rucError').classList.remove('visible');
    });
    
    // Reaparecer el globo de error si el usuario vuelve al campo
    document.getElementById('ruc').addEventListener('focus', function () {
        if (!this.classList.contains('is-valid') && this.value.trim() !== "") {
            document.getElementById('rucError').classList.add('visible');
        }
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

    // ** Validación del correo electrónico (Empresas) **
    function validateEmailEmpresa() {
        const emailInput = document.getElementById('emailEmpresa');
        const errorTooltip = document.getElementById('emailEmpresaError');
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const isValid = emailPattern.test(emailInput.value.trim());

        errorTooltip.classList.toggle('visible', !isValid && emailInput.value.trim() !== "");
        emailInput.classList.toggle('is-valid', isValid);
        emailInput.classList.toggle('is-invalid', !isValid && emailInput.value.trim() !== "");
    }
    toggleTooltipOnInput(document.getElementById('emailEmpresa'), document.getElementById('emailEmpresaError'), validateEmailEmpresa);


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

    // ** Validación del Número de Teléfono (Empresas) **
    function validatePhoneEmpresa() {
        const phoneInput = document.getElementById('phoneEmpresa');
        const errorTooltip = document.getElementById('phoneEmpresaError');
        const phonePattern = /^\d{9}$/;
        const countryCodePattern = /^\+\d+/;

        if (countryCodePattern.test(phoneInput.value)) {
            errorTooltip.textContent = "No hace falta código de país.";
            errorTooltip.classList.add('visible');
            phoneInput.classList.add('is-invalid');
            phoneInput.classList.remove('is-valid');
        } else {
            const isValid = phonePattern.test(phoneInput.value);
            errorTooltip.textContent = "Debe tener 9 dígitos.";

            errorTooltip.classList.toggle('visible', !isValid && phoneInput.value.trim() !== "");
            phoneInput.classList.toggle('is-valid', isValid);
            phoneInput.classList.toggle('is-invalid', !isValid && phoneInput.value.trim() !== "");
        }
    }
    toggleTooltipOnInput(document.getElementById('phoneEmpresa'), document.getElementById('phoneEmpresaError'), validatePhoneEmpresa);


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

    // ** Validación de la Dirección Fiscal **
    function validateFiscalAddress() {
        const addressInput = document.getElementById('fiscal_address');
        const errorTooltip = document.getElementById('fiscalAddressError');
        const isValid = addressInput.value.trim().length >= 5;

        errorTooltip.classList.toggle('visible', !isValid && addressInput.value.trim() !== "");
        addressInput.classList.toggle('is-valid', isValid);
        addressInput.classList.toggle('is-invalid', !isValid && addressInput.value.trim() !== "");
    }
    toggleTooltipOnInput(document.getElementById('fiscal_address'), document.getElementById('fiscalAddressError'), validateFiscalAddress);


    // ###
    // Validación del sector
    // ###

    // Seleccionar elementos
    const industrySelect = document.getElementById('industry');
    const otherIndustryContainer = document.getElementById('otherIndustryContainer');
    const otherIndustryInput = document.getElementById('otherIndustry');
    const form = document.querySelector("form");

    // Agregar evento de cambio al selector
    industrySelect.addEventListener('change', function () {
        if (this.value === 'Otro') {
            otherIndustryContainer.classList.remove('d-none');
            otherIndustryContainer.classList.add('d-flex');
            otherIndustryInput.setAttribute('required', 'true');
            this.classList.remove('is-valid', 'is-invalid'); // Resetear validación
        } else {
            otherIndustryContainer.classList.remove('d-flex');
            otherIndustryContainer.classList.add('d-none');
            otherIndustryInput.removeAttribute('required');
            otherIndustryInput.value = '';
            otherIndustryInput.classList.remove('is-valid', 'is-invalid'); // Resetear validación

            // Validación del select (Solo si se elige algo distinto de "Otro")
            if (this.value === "") {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            }
        }
    });

    // Al enviar el formulario, verificar que se haya seleccionado un sector válido
    form.addEventListener("submit", function (event) {
        if (industrySelect.value === "") {
            event.preventDefault(); // Evita el envío del formulario si no hay selección
            industrySelect.classList.add("is-invalid");
            industrySelect.focus();
        } else if (industrySelect.value === "Otro" && otherIndustryInput.value.trim() === "") {
            event.preventDefault(); // Evita el envío si "Otro" está vacío
            otherIndustryInput.classList.add("is-invalid");
            otherIndustryInput.focus();
        } else if (industrySelect.value === "Otro") {
            // **Solución: Crear un campo hidden para enviar el valor del sector**
            let hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "industry"; // Este se enviará como si fuera el select
            hiddenInput.value = otherIndustryInput.value.trim();
            form.appendChild(hiddenInput);
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

    // Al enviar el formulario, asegurarse de enviar el valor correcto del sector
    document.querySelector("form").addEventListener("submit", function (event) {
        if (industrySelect.value === "Otro" && otherIndustryInput.value.trim() !== "") {
            industrySelect.value = otherIndustryInput.value.trim(); // Reemplaza el valor del select
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

    // Evento para ocultar el tooltip cuando el input pierde el foco
    document.getElementById('passwordEmpresa').addEventListener('blur', function () {
        const tooltip = document.getElementById('passwordTooltipEmpresa');
        tooltip.style.opacity = "0";
        tooltip.style.visibility = "hidden";
        tooltip.style.transform = "translateY(-5px)";
    });

    // Obtener elementos
    const passwordEmpresa = document.getElementById('passwordEmpresa');
    const confirmPasswordEmpresa = document.getElementById('confirmPasswordEmpresa');

    // Habilitar "Confirmar contraseña" solo cuando "Contraseña" tenga valor
    passwordEmpresa.addEventListener('input', function () {
        if (this.value.trim() !== "") {
            confirmPasswordEmpresa.removeAttribute('disabled'); // Habilitar
        } else {
            confirmPasswordEmpresa.setAttribute('disabled', 'true'); // Inhabilitar
        }
    
        // Validar "Confirmar contraseña", pero SIN mostrar el globo de error
        validateConfirmPasswordWithoutError('passwordEmpresa', 'confirmPasswordEmpresa', 'confirmPasswordError');
    });

    // Validación de confirmación de contraseña
    document.getElementById('confirmPasswordEmpresa').addEventListener('input', function () {
        validateConfirmPassword('passwordEmpresa', 'confirmPasswordEmpresa', 'confirmPasswordError');
    });

    // Ocultar el globo de error cuando el usuario pierde el foco
    document.getElementById('confirmPasswordEmpresa').addEventListener('blur', function () {
        document.getElementById('confirmPasswordError').classList.remove('visible');
    });

    // Reaparecer el globo de error si el usuario vuelve al campo
    document.getElementById('confirmPasswordEmpresa').addEventListener('focus', function () {
        if (!this.classList.contains('is-valid') && this.value.trim() !== "") {
            document.getElementById('confirmPasswordError').classList.add('visible');
        }
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

    // Función para manejar la visibilidad del tooltip
    function toggleTooltipOnInput(inputElement, errorTooltip, validationFunction) {
        inputElement.addEventListener('input', function () {
            validationFunction(); // Ejecutar la validación en cada entrada de texto
        });

        inputElement.addEventListener('focus', function () {
            if (inputElement.classList.contains('is-invalid')) {
                errorTooltip.classList.add('visible'); // Volver a mostrar si hay error
            }
        });

        inputElement.addEventListener('blur', function () {
            errorTooltip.classList.remove('visible'); // Ocultar cuando el usuario sale del campo
        });
    }

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
    
        // Definir el estado por defecto (Contraseña débil)
        let strength = {
            percent: 0,
            colorClass: 'bg-danger',
            message: 'Contraseña débil'
        };
    
        if (password !== '') {
            // Evaluar la fuerza de la contraseña si tiene contenido
            strength = evaluatePasswordStrength(password);
        }
    
        // Aplicar estilos a la barra y al texto
        strengthBar.style.width = strength.percent + '%';
        strengthBar.className = 'progress-bar ' + strength.colorClass;
        strengthText.textContent = strength.message;
    
        if (password === '') {
            // Ocultar el tooltip con transición, pero sin borrar el mensaje
            tooltip.style.opacity = "0";
            tooltip.style.visibility = "hidden";
            tooltip.style.transform = "translateY(-5px)";
    
            // No marcar inválido si está vacío
            passwordElement.classList.remove('is-valid', 'is-invalid');
        } else {
            // Mostrar el tooltip con transición suave
            tooltip.style.visibility = "visible";
            tooltip.style.opacity = "1";
            tooltip.style.transform = "translateY(0)";
    
            // Validar la contraseña como válida solo si tiene 6+ caracteres y es media o fuerte
            const isValid = password.length >= 6 && (strength.message === 'Contraseña media' || strength.message === 'Contraseña fuerte');
            
            passwordElement.classList.toggle('is-valid', isValid);
            passwordElement.classList.toggle('is-invalid', !isValid);
        }
    }

    function validateConfirmPassword(passwordId, confirmPasswordId, errorTooltipId) {
        const passwordElement = document.getElementById(passwordId);
        const confirmPasswordElement = document.getElementById(confirmPasswordId);
        const errorTooltip = document.getElementById(errorTooltipId);
    
        const passwordValue = passwordElement.value;
        const confirmPasswordValue = confirmPasswordElement.value;
    
        // Evaluar la fortaleza de la contraseña principal
        const passwordStrength = evaluatePasswordStrength(passwordValue);
        const passwordValid = passwordValue.length >= 6 && (passwordStrength.message === 'Contraseña media' || passwordStrength.message === 'Contraseña fuerte');
    
        // Verificar si coinciden las contraseñas
        const isMatch = passwordValue === confirmPasswordValue && confirmPasswordValue.length >= 6;
    
        // Aplicar Bootstrap validation
        confirmPasswordElement.classList.toggle('is-valid', isMatch && passwordValid);
        confirmPasswordElement.classList.toggle('is-invalid', !isMatch || !passwordValid);
    
        // Mostrar mensaje de error adecuado
        if (!passwordValid) {
            errorTooltip.textContent = "Contraseña débil";
            errorTooltip.classList.add('visible');
        } else if (!isMatch) {
            errorTooltip.textContent = "Las contraseñas no coinciden";
            errorTooltip.classList.add('visible');
        } else {
            errorTooltip.classList.remove('visible');
        }
    }

    function validateConfirmPasswordWithoutError(passwordId, confirmPasswordId, errorTooltipId) {
        const passwordElement = document.getElementById(passwordId);
        const confirmPasswordElement = document.getElementById(confirmPasswordId);
        const errorTooltip = document.getElementById(errorTooltipId);
    
        const passwordValue = passwordElement.value;
        const confirmPasswordValue = confirmPasswordElement.value;
    
        // Evaluar la fortaleza de la contraseña principal
        const passwordStrength = evaluatePasswordStrength(passwordValue);
        const passwordValid = passwordValue.length >= 6 && (passwordStrength.message === 'Contraseña media' || passwordStrength.message === 'Contraseña fuerte');
    
        // Verificar si coinciden las contraseñas
        const isMatch = passwordValid && passwordValue === confirmPasswordValue && confirmPasswordValue.length >= 6;
    
        // Aplicar Bootstrap validation SIN mostrar el globo de error
        confirmPasswordElement.classList.toggle('is-valid', isMatch);
        confirmPasswordElement.classList.toggle('is-invalid', !isMatch);
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