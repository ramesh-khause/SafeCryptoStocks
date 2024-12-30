	document.addEventListener('DOMContentLoaded', async () => {
	    const portfolioList = document.getElementById('portfolio-list');
	    const stockList = document.getElementById('assets-table-body');
	    const currentBalanceElement = document.getElementById('current-balance');
	    const profitLossElement = document.getElementById('profit-loss');

	    try {
	        const portfolioResponse = await fetch(`/portfolios/user`);
	        if (!portfolioResponse.ok) {
	            throw new Error('Failed to fetch portfolio data');
	        }

	        const portfolios = await portfolioResponse.json();
	        if (portfolios.length > 0) {
	            portfolios.forEach((portfolio) => {
	                const portfolioCard = document.createElement('div');
	                portfolioCard.classList.add('portfolio-card');

	                portfolioCard.innerHTML = `
	                    <div class="card-actions">
	                        <button class="edit-portfolio" data-portfolio-id="${portfolio.portfolioId}">📝</button>
	                        <button class="delete-portfolio" data-portfolio-id="${portfolio.portfolioId}">🗑️</button>
	                    </div>
	                    <h3 class="portfolio-name">${portfolio.portfolioName}</h3>
	                    <p class="investment-agenda">${portfolio.investmentAgenda}</p>
	                    <p class="portfolio-id">Portfolio ID: <strong>${portfolio.portfolioId}</strong></p>
	                    <button class="view-stocks" data-portfolio-id="${portfolio.portfolioId}">View Stocks</button>
	                `;

	                portfolioList.appendChild(portfolioCard);
	            });

	            // Add event listeners for viewing stocks
	            document.querySelectorAll('.view-stocks').forEach((button) => {
	                button.addEventListener('click', async (e) => {
	                    const portfolioId = e.target.getAttribute('data-portfolio-id');
	                    await fetchStocksByPortfolio(portfolioId);
	                    await fetchPortfolioSummary(portfolioId);
	                });
	            });

	            // Add event listeners for editing portfolios
	            document.querySelectorAll('.edit-portfolio').forEach((button) => {
	                button.addEventListener('click', async (e) => {
	                    const portfolioId = e.target.getAttribute('data-portfolio-id');
	                    const editUrl = `/portfolios/${portfolioId}`;

	                    const { value: formValues } = await Swal.fire({
	                        title: 'Edit Portfolio',
	                        html:
	                            '<input id="portfolio-name" class="swal2-input" placeholder="Updated Portfolio Name">' +
	                            '<input id="investment-agenda" class="swal2-input" placeholder="Updated Investment Agenda">',
	                        focusConfirm: false,
	                        preConfirm: () => {
	                            return {
	                                portfolioName: document.getElementById('portfolio-name').value,
	                                investmentAgenda: document.getElementById('investment-agenda').value,
	                            };
	                        },
	                    });

	                    if (formValues) {
	                        try {
	                            const response = await fetch(editUrl, {
	                                method: 'PUT',
	                                headers: {
	                                    'Content-Type': 'application/json',
	                                },
	                                body: JSON.stringify(formValues),
	                            });

	                            if (response.ok) {
	                                Swal.fire('Success', 'Portfolio updated successfully!', 'success');
	                                location.reload();
	                            } else {
	                                Swal.fire('Error', 'Failed to update portfolio.', 'error');
	                            }
	                        } catch (error) {
	                            console.error('Error updating portfolio:', error);
	                        }
	                    }
	                });
	            });

	            // Add event listeners for deleting portfolios
	            document.querySelectorAll('.delete-portfolio').forEach((button) => {
	                button.addEventListener('click', async (e) => {
	                    const portfolioId = e.target.getAttribute('data-portfolio-id');
	                    const deleteUrl = `/portfolios/${portfolioId}`;

	                    const result = await Swal.fire({
	                        title: 'Are you sure?',
	                        text: 'Do you really want to delete this portfolio?',
	                        icon: 'warning',
	                        showCancelButton: true,
	                        confirmButtonText: 'Yes, delete it!',
	                        cancelButtonText: 'No, keep it',
	                    });

	                    if (result.isConfirmed) {
	                        try {
	                            const response = await fetch(deleteUrl, {
	                                method: 'DELETE',
	                            });

	                            if (response.ok) {
	                                Swal.fire('Deleted!', 'Portfolio has been deleted.', 'success');
	                                e.target.closest('.portfolio-card').remove();
	                            } else {
	                                Swal.fire('Error', 'Failed to delete portfolio.', 'error');
	                            }
	                        } catch (error) {
	                            console.error('Error deleting portfolio:', error);
	                        }
	                    }
	                });
	            });
	        } else {
	            portfolioList.innerHTML = '<li>No portfolios found</li>';
	        }
	    } catch (error) {
	        console.error('Error fetching portfolios:', error);
	        Swal.fire('Error', 'An error occurred while loading your portfolios. Please try again later.', 'error');
	    }
	

	// Include SweetAlert2 CSS and JS files in your HTML
	// 


		// Fetch and display stocks for a specific portfolio
		async function fetchStocksByPortfolio(portfolioId) {
		    try {
		        const stockResponse = await fetch(`/stock/${portfolioId}`);
		        if (!stockResponse.ok) {
		            throw new Error(`Failed to fetch stocks: ${stockResponse.status}`);
		        }

		        const stocks = await stockResponse.json();
		        stockList.innerHTML = ''; // Clear existing rows

		        if (stocks.length > 0) {
		            stocks.forEach((stock, index) => {
		                const row = document.createElement('tr');
		                row.innerHTML = `
		                    <td>${index + 1}</td>
		                    <td>${stock.stockName}</td>
		                    <td>${stock.currentPrice}</td>
		                    <td class="${stock.percentChange24h >= 0 ? 'positive' : 'negative'}">${stock.percentChange24h}%</td>
		                    <td>${stock.holdings}</td>
		                    <td>${stock.avgBuyPrice}</td>
		                    <td class="${stock.profitLoss >= 0 ? 'positive' : 'negative'}">${stock.profitLoss}</td>
		                    <td>
		                        <button class="sell-stock" data-stock-id="${stock.id}" data-portfolio-id="${portfolioId}"
								data-current-price="${stock.currentPrice}"
								data-avg-buy-price="${stock.avgBuyPrice}"
								>
		                            Sell
		                        </button>
		                    </td>
		                `;
		                stockList.appendChild(row);
		            });

		            // Attach event listeners to the sell buttons
		            document.querySelectorAll('.sell-stock').forEach(button => {
		                button.addEventListener('click', handleSellStock);
		            });
		        } else {
		            stockList.innerHTML = '<tr><td colspan="8">No stocks found</td></tr>';
		        }
		    } catch (error) {
		        console.error('Error fetching stocks:', error);
		        alert('An error occurred while loading stocks. Please try again later.');
		    }
		}

		// Handle the sell stock action
		// Handle the sell stock action
		async function handleSellStock(event) {
		    const button = event.target;
		    const stockId = button.getAttribute('data-stock-id');
		    const portfolioId = button.getAttribute('data-portfolio-id');
		    const sellPrice = button.getAttribute('data-current-price');
		    const avgBuyPrice = button.getAttribute('data-avg-buy-price');

		    // Display a modal for the user to enter the quantity to sell
		    const { value: formValues } = await Swal.fire({
		        title: 'Sell Stock',
		        html: `
		            <div>
		                <label for="sell-quantity">Enter Quantity to Sell:</label>
		                <input id="sell-quantity" class="swal2-input" type="number" min="0" placeholder="Quantity" />
		            </div>
		        `,
		        focusConfirm: false,
		        preConfirm: () => {
		            const quantity = document.getElementById('sell-quantity').value;
		            if (!quantity || isNaN(quantity) || quantity <= 0) {
		                Swal.showValidationMessage('Please enter a valid quantity greater than 0.');
		                return null;
		            }
		            return {
		                sellQuantity: parseFloat(quantity),
		            };
		        },
		    });

		    if (!formValues) {
		        // User canceled the modal
		        return;
		    }

		    const { sellQuantity } = formValues;

		    try {
		        // Call the sell stock endpoint
		        const response = await fetch(`/stock/${portfolioId}/sell/${stockId}`, {
		            method: 'POST',
		            headers: {
		                'Content-Type': 'application/json',
		            },
		            body: JSON.stringify({
		                sellQuantity,
		                sellPrice: parseFloat(sellPrice),
		                avgBuyPrice: parseFloat(avgBuyPrice),
		            }),
		        });

		        if (response.ok) {
		            const message = await response.text();
		            Swal.fire('Success', message, 'success');

		            // Refresh stock list after selling
		            await fetchStocksByPortfolio(portfolioId);
		        } else {
		            const errorMessage = await response.text();
		            Swal.fire('Error', `Failed to sell stock: ${errorMessage}`, 'error');
		        }
		    } catch (error) {
		        console.error('Error selling stock:', error);
		        Swal.fire('Error', 'An error occurred while processing the sale. Please try again later.', 'error');
		    }
		}

	    // Fetch portfolio summary (current balance and profit/loss)
	    async function fetchPortfolioSummary(portfolioId) {
	        try {
	            const response = await fetch(`/portfolios/${portfolioId}/summary`);
	            if (!response.ok) {
	                throw new Error('Failed to fetch portfolio summary');
	            }

	            const summary = await response.json();
	            const { totalValue, totalProfitLoss } = summary;

	            currentBalanceElement.textContent = `₹${totalValue.toFixed(2)}`;
	            profitLossElement.textContent = `${totalProfitLoss >= 0 ? '+' : ''}₹${totalProfitLoss.toFixed(2)}`;

	            profitLossElement.classList.toggle('positive', totalProfitLoss >= 0);
	            profitLossElement.classList.toggle('negative', totalProfitLoss < 0);
	        } catch (error) {
	            console.error('Error fetching portfolio summary:', error);
	            alert('An error occurred while loading portfolio summary.');
	        }
	    }
	});

	// Logout functionality
	const logoutButton = document.getElementById('logout-btn');

	if (logoutButton) {
	    logoutButton.addEventListener('click', function () {
	        fetch('/logout', {
	            method: 'POST',
	            credentials: 'include',
	        })
	        .then(response => {
	            if (response.ok) {
					window.location.href = '/logout?login';
								window.history.pushState(null, null, '/login');
	            } else {
	                console.error('Logout failed');
	                alert('Logout failed. Please try again.');
	            }
	        })
	        .catch(error => {
	            console.error('Error:', error);
	            alert('An error occurred during logout. Please try again.');
	        });
	    });
	} else {
	    console.error('Logout button not found in the DOM.');
	}
