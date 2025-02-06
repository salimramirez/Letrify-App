document.addEventListener('DOMContentLoaded', function () {
    
    document.getElementById('emailEmpresa').addEventListener('input', function () {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        validateField(this, 'emailEmpresaValidIcon', 'emailEmpresaInvalidIcon', emailPattern);
    });

    // Validar el campo de Correo Electrónico para Personas
    document.getElementById('emailPersona').addEventListener('input', function () {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;  // Patrón para correos electrónicos válidos
        validateField(this, 'emailPersonaValidIcon', 'emailPersonaInvalidIcon', emailPattern);
    });

    document.getElementById('passwordEmpresa').addEventListener('input', function () {
        handlePasswordValidation(this, 'passwordStrengthBarEmpresa', 'passwordStrengthTextEmpresa');
    });

    document.getElementById('confirmPasswordEmpresa').addEventListener('input', function () {
        handleConfirmPasswordValidation('passwordEmpresa', 'confirmPasswordEmpresa', 'confirmPasswordValidIconEmpresa', 'confirmPasswordInvalidIconEmpresa');
    });

    document.getElementById('passwordPersona').addEventListener('input', function () {
        handlePasswordValidation(this, 'passwordStrengthBarPersona', 'passwordStrengthTextPersona');
    });

    document.getElementById('confirmPasswordPersona').addEventListener('input', function () {
        handleConfirmPasswordValidation('passwordPersona', 'confirmPasswordPersona', 'confirmPasswordValidIconPersona', 'confirmPasswordInvalidIconPersona');
    });

    // Función para Validar Campos
    function validateField(inputElement, validIconId, invalidIconId, pattern = /.+/) {
        const value = inputElement.value;
        const isValid = pattern.test(value);
        toggleIcons(validIconId, invalidIconId, isValid);
    }

    // Mostrar o Ocultar Íconos según sea válido o no
    function toggleIcons(validIconId, invalidIconId, isValid) {
        document.getElementById(validIconId).classList.toggle('d-none', !isValid);
        document.getElementById(invalidIconId).classList.toggle('d-none', isValid);
    }

    function handlePasswordValidation(passwordElement, strengthBarId, strengthTextId) {
        const password = passwordElement.value;
        const strengthBar = document.getElementById(strengthBarId);
        const strengthText = document.getElementById(strengthTextId);

        if (password === '') {
            strengthBar.style.width = '0%';
            strengthBar.className = 'progress-bar';
            strengthText.textContent = 'La contraseña debe tener al menos 6 caracteres.';
            return;
        }

        const strength = evaluatePasswordStrength(password);
        strengthBar.style.width = strength.percent + '%';
        strengthBar.className = 'progress-bar ' + strength.colorClass;
        strengthText.textContent = strength.message;
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

    // Alternar entre Empresas y Personas y Deshabilitar Campos Ocultos
    document.getElementById('empresaBtn').addEventListener('click', function() {
        document.getElementById('empresaFields').classList.remove('d-none');
        document.getElementById('personaFields').classList.add('d-none');
        this.classList.add('active');
        document.getElementById('personaBtn').classList.remove('active');

        toggleFieldset('empresaFields', false);
        toggleFieldset('personaFields', true);
    });

    document.getElementById('personaBtn').addEventListener('click', function() {
        document.getElementById('personaFields').classList.remove('d-none');
        document.getElementById('empresaFields').classList.add('d-none');
        this.classList.add('active');
        document.getElementById('empresaBtn').classList.remove('active');

        toggleFieldset('personaFields', false);
        toggleFieldset('empresaFields', true);
    });

    // Funcíon para habilitar o deshabilitar campos ocultos
    function toggleFieldset(fieldsetId, disable) {
        const fields = document.querySelectorAll(`#${fieldsetId} input`);
        fields.forEach(field => {
            field.disabled = disable;
        });
    }

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

});