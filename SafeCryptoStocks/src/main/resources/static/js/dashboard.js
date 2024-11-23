document.addEventListener('DOMContentLoaded', () => {
    // Retrieve user data from localStorage
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));

    if (loggedInUser && loggedInUser.username) {
        // Set the userName dynamically
        document.getElementById('user-name').innerText = `Hello, ${loggedInUser.username}`;
    } else {
        // Redirect to login if user data is not available
        window.location.href = '/login';
    }

    // Handle logout button click
    document.getElementById('logout-btn').addEventListener('click', () => {
        // Clear user data from localStorage and redirect to login
        localStorage.removeItem('loggedInUser');
        window.location.href = '/login';
    });
});

// Error handling for network or other errors
window.addEventListener('error', (event) => {
    console.error('An error occurred:', event.message);
    alert('An unexpected error occurred. Please try again later.');
});
