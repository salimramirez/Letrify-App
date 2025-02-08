document.addEventListener('DOMContentLoaded', function () {
    
    // ==========================
    // CONFIGURACIÓN INICIAL AL CARGAR LA PÁGINA
    // ==========================

    const empresaBtn = document.getElementById('empresaBtn');
    const personaBtn = document.getElementById('personaBtn');
    const userTypeInput = document.getElementById('userTypeInput');
    const passwordEmpresa = document.getElementById('passwordEmpresa');
    const confirmPasswordEmpresa = document.getElementById('confirmPasswordEmpresa');
    const passwordPersona = document.getElementById('passwordPersona');
    const confirmPasswordPersona = document.getElementById('confirmPasswordPersona');

    // Ejecutar la función al cargar la página
    checkAndToggleConfirmPassword(passwordEmpresa, confirmPasswordEmpresa);
    checkAndToggleConfirmPassword(passwordPersona, confirmPasswordPersona);


    // Agregar eventos de input para verificar continuamente
    passwordEmpresa.addEventListener('input', function () {
        checkAndToggleConfirmPassword(passwordEmpresa, confirmPasswordEmpresa);
    });

    passwordPersona.addEventListener('input', function () {
        checkAndToggleConfirmPassword(passwordPersona, confirmPasswordPersona);
    });

    // Evento para el botón de Empresas
    empresaBtn.addEventListener('click', () => {
        empresaBtn.classList.add('active', 'btn-primary');
        personaBtn.classList.remove('active', 'btn-primary');
        personaBtn.classList.add('btn-outline-secondary');

        userTypeInput.value = 'COMPANY';

        // Habilitar el campo de sector
        document.getElementById('industry').removeAttribute('disabled');
        
        checkAndToggleConfirmPassword(passwordEmpresa, confirmPasswordEmpresa); // Verifica nuevamente el estado
    });

    // Evento para el botón de Personas
    personaBtn.addEventListener('click', () => {
        personaBtn.classList.add('active', 'btn-primary');
        empresaBtn.classList.remove('active', 'btn-primary');
        empresaBtn.classList.add('btn-outline-secondary');

        userTypeInput.value = 'INDIVIDUAL';

        // Deshabilitar el campo de sector y limpiar su valor
        document.getElementById('industry').setAttribute('disabled', 'true');
        document.getElementById('industry').value = "";

        checkAndToggleConfirmPassword(passwordPersona, confirmPasswordPersona); // Verifica nuevamente el estado
    });

    // Verifica si hay un valor en el input oculto
    window.addEventListener('load', function () {
        if (userTypeInput.value === 'INDIVIDUAL') {
            personaBtn.click();
        } else {
            empresaBtn.click();
        }
    });
    
    // Asegurar que al cargar la página los campos ocultos estén deshabilitados
    toggleFieldset('personaFields', true);

    // Verifica el valor de la contraseña al cambiar entre vistas
    function checkAndToggleConfirmPassword(passwordInput, confirmPasswordInput) {
        if (passwordInput.value.trim() !== "") {
            confirmPasswordInput.removeAttribute('disabled');
        } else {
            confirmPasswordInput.setAttribute('disabled', 'true');
            confirmPasswordInput.value = "";  // Limpia el valor cuando se deshabilita
        }
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

    // Funcíon para habilitar o deshabilitar campos ocultos
    // function toggleFieldset(fieldsetId, disable) {
    //     const fields = document.querySelectorAll(`#${fieldsetId} input`);
    //     fields.forEach(field => {
    //         if (field.id !== 'passwordPersona' && field.id !== 'confirmPasswordPersona' &&
    //             field.id !== 'passwordEmpresa' && field.id !== 'confirmPasswordEmpresa') {
    //             field.disabled = disable;
    //         }
    //     });
    // }

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

    form.addEventListener("submit", function (event) {
        if (userTypeInput.value === "COMPANY") {
            // Elimina todos los campos de la sección Personas
            document.querySelectorAll("#personaFields input, #personaFields select").forEach(field => {
                field.remove();
            });
        } else if (userTypeInput.value === "INDIVIDUAL") {
            // Elimina todos los campos de la sección Empresas
            document.querySelectorAll("#empresaFields input, #empresaFields select").forEach(field => {
                field.remove();
            });
        }
    });

    // Al enviar el formulario, verificar que se haya seleccionado un sector válido
    form.addEventListener("submit", function (event) {
        if (userTypeInput.value === "COMPANY") {
            if (industrySelect.value === "") {
                event.preventDefault(); // Evita el envío del formulario si no hay selección
                industrySelect.classList.add("is-invalid");
                industrySelect.focus();
            } else if (industrySelect.value === "Otro" && otherIndustryInput.value.trim() === "") {
                event.preventDefault(); // Evita el envío si "Otro" está vacío
                otherIndustryInput.classList.add("is-invalid");
                otherIndustryInput.focus();
            } else if (industrySelect.value === "Otro" && otherIndustryInput.value.trim() !== "") {
                // Reemplaza el valor del select por el del input "Otro"
                let hiddenInput = document.createElement("input");
                hiddenInput.type = "hidden";
                hiddenInput.name = "industry"; // Se enviará como si fuera el select
                hiddenInput.value = otherIndustryInput.value.trim();
                form.appendChild(hiddenInput);
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

    // === VALIDACIONES DE PERSONA ===

    // Validación del Nombre Completo (Personas)
    document.getElementById('full_name').addEventListener('input', function () {
        const fullNameInput = this;
        const minLength = 8;  // Mínimo de 8 caracteres
        const isValid = fullNameInput.value.trim().length >= minLength;

        // Manejar tooltip de error
        const errorTooltip = document.getElementById('fullNameError');
        errorTooltip.classList.toggle('visible', !isValid && fullNameInput.value.trim() !== "");

        // Aplicar Bootstrap validation
        fullNameInput.classList.toggle('is-valid', isValid);
        fullNameInput.classList.toggle('is-invalid', !isValid && fullNameInput.value.trim() !== "");
    });

    // Ocultar el globo de error cuando el usuario pierde el foco
    document.getElementById('full_name').addEventListener('blur', function () {
        document.getElementById('fullNameError').classList.remove('visible');
    });

    // Reaparecer el globo de error si el usuario vuelve al campo
    document.getElementById('full_name').addEventListener('focus', function () {
        if (!this.classList.contains('is-valid') && this.value.trim() !== "") {
            document.getElementById('fullNameError').classList.add('visible');
        }
    });

    // Validación del DNI (Personas)
    document.getElementById('dni').addEventListener('input', function () {
        const dniInput = this;
        const dniPattern = /^\d{8}$/;  // Exactamente 8 dígitos
        const isValid = dniPattern.test(dniInput.value);

        // Manejar tooltip de error
        const errorTooltip = document.getElementById('dniError');
        errorTooltip.classList.toggle('visible', !isValid && dniInput.value.trim() !== "");

        // Aplicar Bootstrap validation
        dniInput.classList.toggle('is-valid', isValid);
        dniInput.classList.toggle('is-invalid', !isValid && dniInput.value.trim() !== "");
    });

    // Ocultar el globo de error cuando el usuario pierde el foco
    document.getElementById('dni').addEventListener('blur', function () {
        document.getElementById('dniError').classList.remove('visible');
    });

    // Reaparecer el globo de error si el usuario vuelve al campo
    document.getElementById('dni').addEventListener('focus', function () {
        if (!this.classList.contains('is-valid') && this.value.trim() !== "") {
            document.getElementById('dniError').classList.add('visible');
        }
    });

    // Validación del correo electrónico (Personas)
    document.getElementById('emailPersona').addEventListener('input', function () {
        const emailInput = this; // Referencia al input
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Expresión regular para validar correos
        const isValid = emailPattern.test(emailInput.value);
    
        // Obtener el tooltip de error
        const errorTooltip = document.getElementById('emailPersonaError');
    
        // Manejar tooltip de error con fade-in/out
        errorTooltip.classList.toggle('visible', !isValid && emailInput.value.trim() !== "");
    
        // Aplicar clases de Bootstrap para validación
        emailInput.classList.toggle('is-valid', isValid);
        emailInput.classList.toggle('is-invalid', !isValid && emailInput.value.trim() !== "");
    });

    function validateEmailPersona() {
        const emailInput = document.getElementById('emailPersona');
        const errorTooltip = document.getElementById('emailPersonaError');
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const isValid = emailPattern.test(emailInput.value.trim());
    
        errorTooltip.classList.toggle('visible', !isValid && emailInput.value.trim() !== "");
        emailInput.classList.toggle('is-valid', isValid);
        emailInput.classList.toggle('is-invalid', !isValid && emailInput.value.trim() !== "");
    }

    // Configurar el evento para activar la función al escribir en el input
    toggleTooltipOnInput(document.getElementById('emailPersona'), document.getElementById('emailPersonaError'), validateEmailPersona);

    // Validación del Número de Teléfono (Personas)
    document.getElementById('phonePersona').addEventListener('input', function () {
        const phoneInput = this;
        const phonePattern = /^\d{9}$/;  // Exactamente 9 dígitos
        const countryCodePattern = /^\+\d+/;  // Detecta si el usuario ingresa un código de país (ejemplo: +51, +34)
        const errorTooltip = document.getElementById('phonePersonaError');

        if (countryCodePattern.test(phoneInput.value)) {
            // Si el usuario ingresa un código de país, mostrar un mensaje especial
            errorTooltip.textContent = "No hace falta código de país.";
            errorTooltip.classList.add('visible');
            phoneInput.classList.add('is-invalid');
            phoneInput.classList.remove('is-valid');
        } else {
            // Validar solo si son 9 dígitos
            const isValid = phonePattern.test(phoneInput.value);
            errorTooltip.textContent = "Debe tener 9 dígitos.";  // Mensaje normal

            // Mostrar u ocultar el tooltip según la validez
            errorTooltip.classList.toggle('visible', !isValid && phoneInput.value.trim() !== "");
            
            // Aplicar clases de Bootstrap para borde verde/rojo
            phoneInput.classList.toggle('is-valid', isValid);
            phoneInput.classList.toggle('is-invalid', !isValid && phoneInput.value.trim() !== "");
        }
    });

    // Ocultar el globo de error cuando el usuario pierde el foco
    document.getElementById('phonePersona').addEventListener('blur', function () {
        document.getElementById('phonePersonaError').classList.remove('visible');
    });

    // Reaparecer el globo de error si el usuario vuelve al campo
    document.getElementById('phonePersona').addEventListener('focus', function () {
        if (!this.classList.contains('is-valid') && this.value.trim() !== "") {
            document.getElementById('phonePersonaError').classList.add('visible');
        }
    });

    // Validación de la Dirección (Personas)
    document.getElementById('address').addEventListener('input', function () {
        const addressInput = this;
        const minLength = 5; // Longitud mínima requerida para que sea válida
        const isValid = addressInput.value.trim().length >= minLength;

        // Manejar tooltip de error con fade-in/out
        const errorTooltip = document.getElementById('addressError');
        errorTooltip.classList.toggle('visible', !isValid && addressInput.value.trim() !== "");

        // Aplicar clases de Bootstrap para validación
        this.classList.toggle('is-valid', isValid);
        this.classList.toggle('is-invalid', !isValid && this.value.trim() !== "");
    });

    // Ocultar el globo de error cuando el usuario pierde el foco
    document.getElementById('address').addEventListener('blur', function () {
        document.getElementById('addressError').classList.remove('visible');
    });

    // Reaparecer el globo de error si el usuario vuelve al campo
    document.getElementById('address').addEventListener('focus', function () {
        if (!this.classList.contains('is-valid') && this.value.trim() !== "") {
            document.getElementById('addressError').classList.add('visible');
        }
    });

    // Validación de la Fecha de Nacimiento (Personas)
    document.getElementById('birth_date').addEventListener('change', function () {
        const birthDateInput = this;
        const birthDate = new Date(birthDateInput.value);
        const today = new Date();
        const errorTooltip = document.getElementById('birthDateError');

        let isValid = true;

        // Verificar si la fecha está en el futuro
        if (birthDate > today) {
            isValid = false;
            errorTooltip.textContent = "La fecha no puede estar en el futuro.";
        }
        
        // Verificar si la persona tiene al menos 18 años (opcional)
        const age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        const dayDiff = today.getDate() - birthDate.getDate();
        
        if (age < 18 || (age === 18 && (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)))) {
            isValid = false;
            errorTooltip.textContent = "Debes tener al menos 18 años.";
        }

        // Mostrar u ocultar el tooltip según la validez
        errorTooltip.classList.toggle('visible', !isValid);
        
        // Aplicar clases de Bootstrap para validación
        birthDateInput.classList.toggle('is-valid', isValid);
        birthDateInput.classList.toggle('is-invalid', !isValid);
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
        validateConfirmPassword('passwordEmpresa', 'confirmPasswordEmpresa', 'confirmPasswordError');
    });

    // Validación de contraseñas (Personas)
    document.getElementById('passwordPersona').addEventListener('input', function () {
        handlePasswordValidation(this, 'passwordStrengthBarPersona', 'passwordStrengthTextPersona', 'passwordTooltipPersona');
    });

    // Evento para ocultar el tooltip cuando el input pierde el foco (Personas)
    document.getElementById('passwordPersona').addEventListener('blur', function () {
        const tooltip = document.getElementById('passwordTooltipPersona');
        tooltip.style.opacity = "0";
        tooltip.style.visibility = "hidden";
        tooltip.style.transform = "translateY(-5px)";
    });

    // Habilitar "Confirmar contraseña" solo cuando "Contraseña" tenga valor (Personas)
    passwordPersona.addEventListener('input', function () {
        if (this.value.trim() !== "") {
            confirmPasswordPersona.removeAttribute('disabled'); // Habilitar
        } else {
            confirmPasswordPersona.setAttribute('disabled', 'true'); // Inhabilitar
        }

        // Validar "Confirmar contraseña", pero SIN mostrar el globo de error (Personas)
        validateConfirmPasswordWithoutError('passwordPersona', 'confirmPasswordPersona', 'confirmPasswordErrorPersona');
    });

    // Validación de confirmación de contraseña (Personas)
    document.getElementById('confirmPasswordPersona').addEventListener('input', function () {
        validateConfirmPassword('passwordPersona', 'confirmPasswordPersona', 'confirmPasswordErrorPersona');
    });

    document.getElementById('confirmPasswordPersona').addEventListener('blur', function () {
        document.getElementById('confirmPasswordErrorPersona').classList.remove('visible');
    });

    document.getElementById('confirmPasswordPersona').addEventListener('focus', function () {
        validateConfirmPassword('passwordPersona', 'confirmPasswordPersona', 'confirmPasswordErrorPersona');
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

        if (tooltip) {
            tooltip.style.opacity = "0";
            tooltip.style.visibility = "hidden";
            tooltip.style.transform = "translateY(-5px)";
        }
        
        // Verifica si los elementos strengthBar, strengthText y tooltip existen
        if (!strengthBar || !strengthText || !tooltip) {
            console.warn(`Uno de los elementos no se encontró: strengthBarId=${strengthBarId}, strengthTextId=${strengthTextId}, tooltipId=${tooltipId}`);
            return; // Salimos de la función si no existe alguno
        }

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
    
        if (!passwordElement || !confirmPasswordElement || !errorTooltip) {
            console.warn("Uno de los elementos de validación no se encontró en el DOM.");
            return;
        }
    
        const passwordValue = passwordElement.value;
        const confirmPasswordValue = confirmPasswordElement.value;
    
        // Si "Confirmar contraseña" está vacío, no aplicar ninguna validación
        if (confirmPasswordValue.trim() === "") {
            confirmPasswordElement.classList.remove('is-valid', 'is-invalid');
            errorTooltip.classList.remove('visible');
            return;
        }
    
        // Evaluar la fortaleza de la contraseña principal y de "Confirmar contraseña"
        const passwordStrength = evaluatePasswordStrength(passwordValue);
        const confirmPasswordStrength = evaluatePasswordStrength(confirmPasswordValue);
    
        let passwordValid = passwordValue.length >= 6 && (passwordStrength.message === 'Contraseña media' || passwordStrength.message === 'Contraseña fuerte');
        let confirmPasswordValid = confirmPasswordValue.length >= 6 && (confirmPasswordStrength.message === 'Contraseña media' || confirmPasswordStrength.message === 'Contraseña fuerte');
    
        // Verificar si coinciden las contraseñas
        let isMatch = passwordValid && confirmPasswordValid && passwordValue === confirmPasswordValue;
    
        // Aplicar Bootstrap validation solo si el usuario ya escribió algo
        confirmPasswordElement.classList.toggle('is-valid', isMatch);
        confirmPasswordElement.classList.toggle('is-invalid', !isMatch || !confirmPasswordValid);
    
        // Mostrar mensaje de error adecuado solo si el campo tiene contenido
        if (!confirmPasswordValid) {
            errorTooltip.textContent = "Contraseña débil";
            errorTooltip.classList.add('visible');
        } else if (!isMatch) {
            errorTooltip.textContent = "Las contraseñas no coinciden";
            errorTooltip.classList.add('visible');
        } else {
            errorTooltip.classList.remove('visible');
        }
    }

    function validateConfirmPasswordWithoutError(passwordId, confirmPasswordId) {
        const passwordElement = document.getElementById(passwordId);
        const confirmPasswordElement = document.getElementById(confirmPasswordId);
    
        if (!passwordElement || !confirmPasswordElement) {
            console.warn("Uno de los elementos de validación no se encontró en el DOM.");
            return;
        }
    
        const passwordValue = passwordElement.value;
        const confirmPasswordValue = confirmPasswordElement.value;
    
        // Si "Confirmar contraseña" está vacío, no aplicar ninguna validación
        if (confirmPasswordValue.trim() === "") {
            confirmPasswordElement.classList.remove('is-valid', 'is-invalid');
            return;
        }
    
        // Evaluar la fortaleza de la contraseña principal
        const passwordStrength = evaluatePasswordStrength(passwordValue);
        let passwordValid = passwordValue.length >= 6 && (passwordStrength.message === 'Contraseña media' || passwordStrength.message === 'Contraseña fuerte');
    
        // Verificar si coinciden las contraseñas
        let isMatch = passwordValid && passwordValue === confirmPasswordValue && confirmPasswordValue.length >= 6;
    
        // Aplicar Bootstrap validation solo si el usuario ya escribió algo
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
    
    // =========================================================
    // VALIDACIONES DE LOS CAMPOS LUEGO DE PRESIONAR "REGISTRAR"
    // =========================================================

    // Validación de Razón Social
    const businessNameInput = document.getElementById("business_name");
    const errorTooltip = document.getElementById("businessNameError");
    
    if (businessNameInput) {
        let previousInvalidValue = businessNameInput.value.trim();
        let previousError = false;  // Flag para saber si el valor anterior fue inválido por backend
    
        businessNameInput.addEventListener("input", function () {
            const currentValue = this.value.trim();
            let isValidLength = currentValue.length >= 5;
    
            if (currentValue === previousInvalidValue && previousError && currentValue !== "") {
                // Mantener el mensaje "Ya está en uso"
                this.classList.add("is-invalid");
                this.classList.remove("is-valid");
                errorTooltip.textContent = "Ya está en uso";
                errorTooltip.classList.add("visible");
            } else if (!isValidLength) {
                // Mostrar el error "Debe tener al menos 5 caracteres"
                this.classList.add("is-invalid");
                this.classList.remove("is-valid");
                errorTooltip.textContent = "Debe tener al menos 5 caracteres";
                errorTooltip.classList.toggle("visible", currentValue !== "");
            } else {
                // Validación exitosa
                this.classList.remove("is-invalid");
                this.classList.add("is-valid");
                errorTooltip.classList.remove("visible");
                
                // Actualizar previousInvalidValue y resetear el flag
                previousInvalidValue = currentValue;
                previousError = false;
            }
        });
    
        businessNameInput.addEventListener("focus", function () {
            const currentValue = this.value.trim();
            if (currentValue !== "" && currentValue === previousInvalidValue && previousError) {
                errorTooltip.textContent = "Ya está en uso";
                errorTooltip.classList.add("visible");
            } else if (currentValue !== "" && currentValue.length < 5) {
                errorTooltip.textContent = "Debe tener al menos 5 caracteres";
                errorTooltip.classList.add("visible");
            } else {
                errorTooltip.classList.remove("visible");
            }
        });
    
        businessNameInput.addEventListener("blur", function () {
            errorTooltip.classList.remove("visible");
        });
    }

    // Validación del RUC
    const rucInput = document.getElementById('ruc');
    const rucErrorTooltip = document.getElementById('rucError');
    
    if (rucInput) {
        let previousInvalidValue = rucInput.value.trim();  // Almacena el valor inválido inicial
        let isErrorFromBackend = rucInput.classList.contains('is-invalid');  // Detecta si el error viene del backend
    
        rucInput.addEventListener('input', function () {
            const currentValue = this.value.trim();
            const rucPattern = /^\d{11}$/;  // Validación para 11 dígitos exactos
            let isValidLength = rucPattern.test(currentValue);
    
            if (currentValue === previousInvalidValue && isErrorFromBackend && currentValue !== "") {
                // Si el valor actual es igual al valor inválido anterior, mantener el error de "Ya está en uso"
                this.classList.add('is-invalid');
                this.classList.remove('is-valid');
                rucErrorTooltip.textContent = "Ya está en uso";
                rucErrorTooltip.classList.add('visible');
            } else if (!isValidLength) {
                // Si no cumple la regla de 11 dígitos, mostrar el error correspondiente
                this.classList.add('is-invalid');
                this.classList.remove('is-valid');
                rucErrorTooltip.textContent = "Deben ser 11 dígitos";
                rucErrorTooltip.classList.toggle('visible', currentValue !== "");
            } else {
                // Si es válido, marcar como válido y resetear el flag de error
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
                rucErrorTooltip.classList.remove('visible');
                isErrorFromBackend = false;  // Resetear el flag solo si el valor es válido
            }
        });
    
        // Evento focus mejorado para mostrar el mensaje adecuado
        rucInput.addEventListener('focus', function () {
            const currentValue = this.value.trim();
            if (currentValue === previousInvalidValue && isErrorFromBackend && currentValue !== "") {
                rucErrorTooltip.textContent = "Ya está en uso";
                rucErrorTooltip.classList.add('visible');
            } else if (currentValue !== "" && currentValue.length < 11) {
                rucErrorTooltip.textContent = "Deben ser 11 dígitos";
                rucErrorTooltip.classList.add('visible');
            } else {
                rucErrorTooltip.classList.remove('visible');
            }
        });
    
        // Ocultar el globo de error cuando el usuario pierde el foco
        rucInput.addEventListener('blur', function () {
            rucErrorTooltip.classList.remove('visible');
        });
    }

    // Validación del correo (Empresas)
    const emailInput = document.getElementById('emailEmpresa');
    const emailErrorTooltip = document.getElementById('emailEmpresaError');
    
    // Solo asignar previousInvalidValue si el backend lo marcó como inválido
    let previousInvalidValue = emailInput.classList.contains('is-invalid') ? emailInput.value.trim() : "";
    
    if (emailInput) {
        emailInput.addEventListener('input', function () {
            const currentValue = this.value.trim();
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            let isValidFormat = emailPattern.test(currentValue);
    
            if (currentValue === "") {
                this.classList.remove('is-valid', 'is-invalid');
                emailErrorTooltip.classList.remove('visible');
                previousInvalidValue = "";
            } else if (currentValue === previousInvalidValue) {
                this.classList.add('is-invalid');
                this.classList.remove('is-valid');
                emailErrorTooltip.textContent = "Correo ya registrado. Por favor, usa otro.";
                emailErrorTooltip.classList.add('visible');
            } else if (!isValidFormat) {
                this.classList.add('is-invalid');
                this.classList.remove('is-valid');
                emailErrorTooltip.textContent = "Ingresa un correo válido.";
                emailErrorTooltip.classList.add('visible');
            } else {
                this.classList.add('is-valid');
                this.classList.remove('is-invalid');
                emailErrorTooltip.classList.remove('visible');
            }
        });
    
        emailInput.addEventListener('focus', function () {
            const currentValue = this.value.trim();
            if (currentValue === previousInvalidValue && currentValue !== "") {
                this.classList.add('is-invalid');
                emailErrorTooltip.textContent = "Correo ya registrado. Por favor, usa otro.";
                emailErrorTooltip.classList.add('visible');
            }
        });
    
        emailInput.addEventListener('blur', function () {
            emailErrorTooltip.classList.remove('visible');
        });
    }

    // Validación del Número de Teléfono
    const phoneInput = document.getElementById('phoneEmpresa');
    const phoneErrorTooltip = document.getElementById('phoneEmpresaError');
    
    if (phoneInput) {
        let previousInvalidValue = phoneInput.classList.contains('is-invalid') ? phoneInput.getAttribute("value") : "";  // Guardar solo si es inválido
    
        phoneInput.addEventListener('input', function () {
            const currentValue = this.value.trim();
            const phonePattern = /^\d{9}$/;
            let isValidFormat = phonePattern.test(currentValue);
    
            if (currentValue === "") {
                // Si el campo está vacío, quitar las clases de validación
                this.classList.remove('is-valid', 'is-invalid');
                phoneErrorTooltip.classList.remove('visible');
            } else if (currentValue === previousInvalidValue) {
                // Mostrar "Número ya registrado"
                this.classList.add('is-invalid');
                this.classList.remove('is-valid');
                phoneErrorTooltip.textContent = "Número ya registrado. Por favor, usa otro.";
                phoneErrorTooltip.classList.add('visible');
            } else if (!isValidFormat) {
                // Mostrar "Debe tener 9 dígitos"
                this.classList.add('is-invalid');
                this.classList.remove('is-valid');
                phoneErrorTooltip.textContent = "Debe tener 9 dígitos.";
                phoneErrorTooltip.classList.add('visible');
            } else {
                // El número es válido
                this.classList.add('is-valid');
                this.classList.remove('is-invalid');
                phoneErrorTooltip.classList.remove('visible');
            }
        });
    
        phoneInput.addEventListener('focus', function () {
            const currentValue = this.value.trim();
            if (currentValue === previousInvalidValue && currentValue !== "") {
                this.classList.add('is-invalid');
                phoneErrorTooltip.textContent = "Número ya registrado. Por favor, usa otro.";
                phoneErrorTooltip.classList.add('visible');
            } else if (!this.classList.contains('is-valid') && currentValue !== "" && currentValue.length < 9) {
                phoneErrorTooltip.textContent = "Debe tener 9 dígitos.";
                phoneErrorTooltip.classList.add('visible');
            }
        });
    
        phoneInput.addEventListener('blur', function () {
            phoneErrorTooltip.classList.remove('visible');
        });
    }

});