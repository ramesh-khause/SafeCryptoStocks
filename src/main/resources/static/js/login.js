document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const otpSection = document.getElementById('otp-section');
    const verifyOtpButton = document.getElementById('verify-otp-button');
    const forgotPasswordLink = document.getElementById('forgot-password');
    const forgotPasswordForm = document.getElementById('forgot-password-form');
    const forgotPasswordSubmitButton = document.getElementById('forgot-password-submit');
    const forgotPasswordOtpSection = document.getElementById('forgot-password-otp-section');
    const forgotPasswordVerifyOtpButton = document.getElementById('forgot-password-verify-otp-button');
    const resetPasswordSection = document.getElementById('reset-password-section');
    const resetPasswordSubmitButton = document.getElementById('reset-password-submit');
    const messageElement = document.getElementById('message');
    const loadingBar = document.getElementById('loading-bar');
    const progressBar = document.querySelector('.progress-bar-indicator');

    // Function to toggle the loading bar visibility
    const toggleLoadingBar = (show) => {
        if (show) {
            loadingBar.style.display = 'block';
            progressBar.style.animation = 'loading 2s linear infinite';
        } else {
            loadingBar.style.display = 'none';
            progressBar.style.animation = 'none';
        }
    };

    // Function to validate credentials format
    const validateCredentials = (email, password) => {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const passwordPattern = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        if (!emailPattern.test(email)) {
            messageElement.textContent = 'Please enter a valid email address.';
            messageElement.style.color = 'red';
            return false;
        }

        if (!passwordPattern.test(password)) {
            messageElement.textContent = 'Wrong Password. Password must contain at least one uppercase letter, one number, and one special character.';
            messageElement.style.color = 'red';
            return false;
        }

        return true;
    };

    if (form) {
        form.addEventListener('submit', (event) => {
            event.preventDefault();

            // Clear previous messages
            messageElement.textContent = '';
            const emailField = document.getElementById('email');
            const passwordField = document.getElementById('password');
            emailField.style.borderColor = '';
            passwordField.style.borderColor = '';

            const email = emailField.value.trim();
            const password = passwordField.value.trim();
            let isValid = true;

            // Check for empty email
            if (!email) {
                const emailError = 'Email is required';
                messageElement.textContent = emailError;
                emailField.style.borderColor = 'red';
                messageElement.style.color = 'red';
                isValid = false;
            }

            // Check for empty password
            if (!password) {
                const passwordError = 'Password is required';
                if (!messageElement.textContent) {
                    messageElement.textContent = passwordError;
                } else {
                    messageElement.textContent += ' | Password is required';
                }
                passwordField.style.borderColor = 'red';
                messageElement.style.color = 'red';
                isValid = false;
            }

            // If invalid, exit
            if (!isValid) return;

            // Start loading animation
            toggleLoadingBar(true);

            // If credentials are valid, proceed with further checks
            if (!validateCredentials(email, password)) {
                toggleLoadingBar(false);
                return;
            }

            // Make a fetch call to the server
            fetch('/loginUser', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
                credentials: 'include',
            })
                .then((response) => response.json())
                .then((data) => {
                    toggleLoadingBar(false);

                    if (data.success) {
                        messageElement.textContent = data.message;
                        messageElement.style.color = 'green';

                        setTimeout(() => {
                            otpSection.style.display = 'block';
                            form.style.display = 'none';
                        }, 2000);
                    } else {
                        messageElement.textContent = data.message || 'Login failed.';
                        messageElement.style.color = 'red';
                    }
                })
                .catch((error) => {
                    console.error('Error:', error);
                    toggleLoadingBar(false);
                    messageElement.textContent = 'An error occurred. Please try again.';
                    messageElement.style.color = 'red';
                });
        });
    }

    if (verifyOtpButton && otpSection) {
        verifyOtpButton.addEventListener('click', () => {
            const email = document.getElementById('email').value;
            const otp = document.getElementById('otp').value;

            toggleLoadingBar(true);

            fetch('/verifyOtp', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: email, otp: otp }),
                credentials: 'include',
            })
                .then((response) => response.json())
                .then((data) => {
                    toggleLoadingBar(false);

                    if (data.success) {
                        window.location.href = '/dashboard';
                    } else {
                        alert(data.message);
                    }
                })
                .catch((error) => {
                    toggleLoadingBar(false);
                    console.error('Error:', error);
                    alert('An error occurred while verifying the OTP. Please try again.');
                });
        });
    }

    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener('click', (event) => {
            event.preventDefault();
            forgotPasswordForm.style.display = 'block';
            form.style.display = 'none';
        });
    }

    if (forgotPasswordSubmitButton) {
        forgotPasswordSubmitButton.addEventListener('click', (event) => {
            event.preventDefault();

            const email = document.getElementById('forgot-email').value;

            fetch('/forgotPassword', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email }),
                credentials: 'include',
            })
                .then((response) => response.json())
                .then((data) => {
                    if (data.success) {
                        alert(data.message);
                        forgotPasswordOtpSection.style.display = 'block';
                        forgotPasswordForm.style.display = 'none';
                    } else {
                        alert(data.message);
                    }
                })
                .catch((error) => {
                    console.error('Error:', error);
                    alert('An error occurred while requesting the OTP. Please try again.');
                });
        });
    }

    if (forgotPasswordVerifyOtpButton && forgotPasswordOtpSection) {
        forgotPasswordVerifyOtpButton.addEventListener('click', (event) => {
            event.preventDefault();

            const email = document.getElementById('forgot-email').value;
            const otp = document.getElementById('forgot-password-otp').value;

            fetch('/verifyPasswordResetOtp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, otp: otp }),
                credentials: 'include',
            })
                .then((response) => response.json())
                .then((data) => {
                    if (data.success) {
                        resetPasswordSection.style.display = 'block';
                        forgotPasswordOtpSection.style.display = 'none';
                    } else {
                        alert(data.message);
                    }
                })
                .catch((error) => {
                    console.error('Error:', error);
                    alert('An error occurred while verifying the OTP. Please try again.');
                });
        });
    }

    if (resetPasswordSubmitButton) {
        resetPasswordSubmitButton.addEventListener('click', (event) => {
            event.preventDefault();

            const newPassword = document.getElementById('new-password').value;

            fetch('/resetPassword', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ newPassword: newPassword }),
                credentials: 'include',
            })
                .then((response) => response.json())
                .then((data) => {
                    if (data.success) {
                        alert('Password reset successful. Please log in with your new password.');
                        window.location.href = '/login';
                    } else {
                        alert(data.message);
                    }
                })
                .catch((error) => {
                    console.error('Error:', error);
                    alert('An error occurred while resetting the password. Please try again.');
                });
        });
    }
});
