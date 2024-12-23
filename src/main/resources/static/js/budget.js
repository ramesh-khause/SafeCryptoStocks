document.addEventListener("DOMContentLoaded", function () {
    const budgetSection = document.getElementById("budget-section");
    const expenseSection = document.getElementById("expense-section");
    const budgetsList = document.getElementById("budgets-list");
    const expensesList = document.getElementById("expense-table-body");
    const addBudgetBtn = document.getElementById("add-budget-btn");
    const addExpenseBtn = document.getElementById("add-expense-btn");
    const backToBudgetsBtn = document.getElementById("back-to-budgets-btn");
    const budgetModal = document.getElementById("budget-modal");
    const expenseModal = document.getElementById("expense-modal");
    const modalBackdrop = document.getElementById("modal-backdrop");
    const saveBudgetBtn = document.getElementById("save-budget-btn");
    const closeBudgetModalBtn = document.getElementById("close-budget-modal");
    const saveExpenseBtn = document.getElementById("save-expense-btn");
    const closeExpenseModalBtn = document.getElementById("close-expense-modal");
    const logoutButton = document.getElementById('logout-btn');

    const API_BASE_URL = "http://localhost:8080";
    let currentBudgetId = null;

    // Helper functions
    function toggleModal(modal, show) {
        modal.classList.toggle("show", show);
        modalBackdrop.classList.toggle("show", show);
    }

    function clearList(list) {
        list.innerHTML = "";
    }

    async function fetchBudgets() {
        clearList(budgetsList);
        try {
            const response = await fetch(`${API_BASE_URL}/budgets`);
            if (!response.ok) throw new Error("Failed to fetch budgets");
            const budgets = await response.json();

            budgets.forEach((budget) => {
                const budgetCard = document.createElement("div");
                budgetCard.classList.add("budget-card");
                budgetCard.setAttribute("data-id", budget.id);

                const progressPercentage = (budget.expenses / budget.amount) * 100;
                const progressColor = progressPercentage >= 90 ? "red" : "blue";

                budgetCard.innerHTML = `
                    <h3>${budget.name}</h3>
                    <p>${budget.description}</p>
                    <p>Total Amount: ₹${budget.amount}</p>
                    <p>Expenses: ₹${budget.expenses}</p>
                    <div class="progress-container">
                        <div class="progress-bar" style="width: ${progressPercentage}%; background-color: ${progressColor};"></div>
                    </div>
                    <br>
                    <button class="view-expenses-btn" data-id="${budget.id}">View Expenses</button>
                    <button class="delete-budget-btn" data-id="${budget.id}">Delete Budget</button>
                `;

                budgetsList.appendChild(budgetCard);
            });

            // Attach event listeners to view expense buttons
            document.querySelectorAll(".view-expenses-btn").forEach((btn) => {
                btn.addEventListener("click", (e) => {
                    const budgetId = e.target.getAttribute("data-id");
                    showExpenses(budgetId);
                });
            });
        } catch (error) {
            console.error("Error fetching budgets:", error);
        }
    }

    async function updateBudget(budgetId, stockAmount, action) {
        try {
            const response = await fetch(`${API_BASE_URL}/budgets/${budgetId}`);
            if (!response.ok) throw new Error("Failed to fetch budget details");

            const budget = await response.json();
            const updatedExpenses = action === "add" ? budget.expenses + stockAmount : budget.expenses - stockAmount;

            const updateResponse = await fetch(`${API_BASE_URL}/budgets/${budgetId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ ...budget, expenses: updatedExpenses }),
            });

            if (updateResponse.ok) {
                alert("Budget updated successfully!");
                fetchBudgets();
            } else {
                alert("Failed to update budget!");
            }
        } catch (error) {
            console.error("Error updating budget:", error);
        }
    }

    async function purchaseStock(budgetId, stockPrice) {
        await updateBudget(budgetId, stockPrice, "add");
    }

    async function deleteStock(budgetId, stockPrice) {
        await updateBudget(budgetId, stockPrice, "subtract");
    }

    async function fetchExpenses(budgetId) {
        clearList(expensesList);
        try {
            const response = await fetch(`${API_BASE_URL}/budgets/${budgetId}/expenses`);
            if (!response.ok) throw new Error("Failed to fetch expenses");
            const expenses = await response.json();

            expenses.forEach((expense, index) => {
                const row = document.createElement("tr");
                const dateTime = new Date(expense.timestamp);
                const formattedDate = dateTime.toLocaleDateString();
                const formattedTime = dateTime.toLocaleTimeString();

                row.innerHTML = `
                    <td>${index + 1}</td>
                    <td>${expense.description}</td>
                    <td>₹${expense.amount}</td>
                    <td>${expense.budgetName}</td>
                    <td>${formattedDate} ${formattedTime}</td>
                `;

                expensesList.appendChild(row);
            });
        } catch (error) {
            console.error("Error fetching expenses:", error);
        }
    }

    function showExpenses(budgetId) {
        currentBudgetId = budgetId;
        budgetSection.style.display = "none";
        expenseSection.style.display = "block";
        fetchExpenses(budgetId);
    }

    async function addBudget() {
        const name = document.getElementById("budget-name").value.trim();
        const description = document.getElementById("budget-description").value.trim();
        const amount = parseFloat(document.getElementById("budget-amount").value);

        if (!name || !description || isNaN(amount) || amount <= 0) {
            alert("Please fill all fields correctly!");
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/budgets`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, description, amount }),
            });

            if (response.ok) {
                toggleModal(budgetModal, false);
                fetchBudgets();
            } else {
                alert("Failed to add budget!");
            }
        } catch (error) {
            console.error("Error adding budget:", error);
        }
    }

    async function addExpense() {
        const name = document.getElementById("expense-name").value.trim();
        const amount = parseFloat(document.getElementById("expense-amount").value);

        if (!name || isNaN(amount) || amount <= 0) {
            alert("Please fill all fields correctly!");
            return;
        }

        try {
            const budgetResponse = await fetch(`${API_BASE_URL}/budgets/${currentBudgetId}`);
            if (!budgetResponse.ok) throw new Error("Failed to fetch budget details");

            const budget = await budgetResponse.json();
            const newTotalExpenses = budget.expenses + amount;

            if (newTotalExpenses > budget.amount) {
                alert(`Out of Budget: Adding this expense will exceed the budget limit of ₹${budget.amount}.`);
                return;
            }

            const response = await fetch(`${API_BASE_URL}/budgets/${currentBudgetId}/expenses`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ description: name, amount }),
            });

            if (response.ok) {
                toggleModal(expenseModal, false);
                fetchExpenses(currentBudgetId);
                fetchBudgets();
            } else {
                alert("Failed to add expense!");
            }
        } catch (error) {
            console.error("Error adding expense:", error);
        }
    }

    async function deleteBudget(budgetId) {
        try {
            const response = await fetch(`${API_BASE_URL}/budgets/${budgetId}`, {
                method: "DELETE",
            });
            if (response.ok) {
                alert("Budget deleted successfully!");
                document.querySelector(`.budget-card[data-id="${budgetId}"]`).remove();
            } else {
                alert("Failed to delete budget!");
            }
        } catch (error) {
            console.error("Error deleting budget:", error);
        }
    }

    // Event Listeners
    addBudgetBtn.addEventListener("click", () => toggleModal(budgetModal, true));
    saveBudgetBtn.addEventListener("click", addBudget);
    closeBudgetModalBtn.addEventListener("click", () => toggleModal(budgetModal, false));
    addExpenseBtn.addEventListener("click", () => toggleModal(expenseModal, true));
    saveExpenseBtn.addEventListener("click", addExpense);
    closeExpenseModalBtn.addEventListener("click", () => toggleModal(expenseModal, false));
    modalBackdrop.addEventListener("click", () => {
        toggleModal(budgetModal, false);
        toggleModal(expenseModal, false);
    });

    backToBudgetsBtn.addEventListener("click", () => {
        expenseSection.style.display = "none";
        budgetSection.style.display = "block";
    });

    document.addEventListener("click", (event) => {
        if (event.target.classList.contains("delete-budget-btn")) {
            const budgetId = event.target.getAttribute("data-id");
            if (confirm("Are you sure you want to delete this budget?")) {
                deleteBudget(budgetId);
            }
        }
    });

    logoutButton.addEventListener('click', async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/logout`, { method: 'POST' });
            if (response.ok) {
                alert("Logged out successfully!");
				window.location.href = '/logout?login';
							window.history.pushState(null, null, '/login');
            } else {
                alert("Failed to log out!");
            }
        } catch (error) {
            console.error("Error during logout:", error);
        }
    });

    // Initial fetch
    fetchBudgets();
});
