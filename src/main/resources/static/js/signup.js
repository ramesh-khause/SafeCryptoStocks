document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("signup-form");
    const inputs = form.querySelectorAll("input");
    const progressBar = document.getElementById("progress-bar");

    // Function to display server-side messages
    const displayServerMessage = (message, type) => {
        const messageContainer = document.createElement("div");
        messageContainer.classList.add("server-message", "p-4", "mb-4", "rounded");
        if (type === "success") {
            messageContainer.classList.add("bg-green-100", "text-green-700");
        } else if (type === "error") {
            messageContainer.classList.add("bg-red-100", "text-red-700");
        }
        messageContainer.textContent = message;
        form.prepend(messageContainer); // Display message at the top of the form
    };

    form.addEventListener("submit", (event) => {
        let formIsValid = true;

        // Clear previous error messages
        const errorElements = form.querySelectorAll(".error-message");
        errorElements.forEach(element => {
            element.textContent = ''; // Clear any previous error message
        });

        const serverMessages = form.querySelectorAll(".server-message");
        serverMessages.forEach(element => {
            element.remove(); // Remove previous server messages
        });

        // Validate each field
        inputs.forEach(input => {
            if (input.hasAttribute("required") && !input.value.trim()) {
                formIsValid = false;
                const label = document.querySelector(`label[for='${input.id}']`);
                const labelText = label ? label.textContent : "Field"; // Fallback if no label is found
                showErrorMessage(input, `${labelText} is required.`);
            }

            // Additional validations (omitted for brevity, as in your original code)
			// Email Validation
			           if (input.id === "email" && input.value.trim() && !/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.com$/.test(input.value)) {
			               formIsValid = false;
			               showErrorMessage(input, "Email must contain @ and domain name...");
			           }

			           // Username Validation
			           if (input.id === "username" && input.value.trim()) {
			               if (input.value.length < 5 || input.value.length > 10) {
			                   formIsValid = false;
			                   showErrorMessage(input, "Username must be between 5 and 10 characters.");
			               }
			               if (!/^[a-zA-Z0-9]+$/.test(input.value)) {
			                   formIsValid = false;
			                   showErrorMessage(input, "Username can only contain alphanumeric characters.");
			               }
			           }

			           // First Name Validation
			           if (input.id === "firstName" && input.value.trim()) {
			               if (input.value.length < 3 || input.value.length > 10) {
			                   formIsValid = false;
			                   showErrorMessage(input, "First name must be between 3 and 10 characters.");
			               }
			               if (!/^[a-zA-Z]+$/.test(input.value)) {
			                   formIsValid = false;
			                   showErrorMessage(input, "First name can only contain letters.");
			               }
			           }

			           // Last Name Validation
			           if (input.id === "lastName" && input.value.trim()) {
			               if (input.value.length < 3 || input.value.length > 10) {
			                   formIsValid = false;
			                   showErrorMessage(input, "Last name must be between 3 and 10 characters.");
			               }
			               if (!/^[a-zA-Z]+$/.test(input.value)) {
			                   formIsValid = false;
			                   showErrorMessage(input, "Last name can only contain letters.");
			               }
			           }

			           // Address Validation
			           if (input.id === "address" && input.value.trim()) {
			               if (input.value.length < 3 || input.value.length > 20) {
			                   formIsValid = false;
			                   showErrorMessage(input, "Address must be between 3 and 20 characters.");
			               }
			               if (!/^[a-zA-Z0-9\s,.'-]+$/.test(input.value)) {
			                   formIsValid = false;
			                   showErrorMessage(input, "Address can only contain letters, numbers, spaces, and certain punctuation.");
			               }
			           }

			           // Password Validation
			           if (input.id === "password" && input.value.trim()) {
			               const password = input.value;
			               const passwordRequirements = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

			               if (!passwordRequirements.test(password)) {
			                   formIsValid = false;
			                   showErrorMessage(input, "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
			               }
			           }
			       });


        // Prevent form submission if not valid
        if (!formIsValid) {
            event.preventDefault();
        } else {
            // Show progress bar
            showProgressBar();

            // Handle server-side messages if available
            fetch(form.action, {
                method: "POST",
                body: new FormData(form),
            })
            .then(response => response.json())
            .then(data => {
                if (data.message && data.messageType === "success") {
                    displayServerMessage(data.message, "success");

                    // Redirect to login page after 2 seconds
                    setTimeout(() => {
                        window.location.href = "/login"; // Replace with your login page URL
                    }, 2000);
                } else {
                    displayServerMessage(data.message || "An error occurred.", "error");

                    // Refresh page after 2 seconds on error
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000);
                }
            })
            .catch(() => {
                displayServerMessage("An error occurred. Please try again.", "error");

                // Refresh page after 2 seconds on error
                setTimeout(() => {
                    window.location.reload();
                }, 2000);
            });

            event.preventDefault(); // Prevent the default form submission to handle via fetch
        }
    });

    function showErrorMessage(input, message) {
        const errorMessage = document.createElement("p");
        errorMessage.classList.add("error-message", "text-red-500", "text-sm", "mt-1");
        errorMessage.textContent = message;
        input.parentNode.appendChild(errorMessage);
    }

    function showProgressBar() {
        progressBar.style.transition = 'width 2s ease'; // Smooth animation
        progressBar.style.width = '100%';

        // Reset progress bar after 3 seconds
        setTimeout(() => {
            progressBar.style.transition = 'none';
            progressBar.style.width = '0%';
        }, 3000);
    }
});
