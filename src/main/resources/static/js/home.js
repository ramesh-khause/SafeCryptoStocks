document.addEventListener("DOMContentLoaded", function () {
    const btcPriceElement = document.getElementById("btc-price");
    const btcChangeElement = document.getElementById("btc-change");
    const ethPriceElement = document.getElementById("eth-price");
    const ethChangeElement = document.getElementById("eth-change");

    const ctx = document.getElementById("marketChart").getContext("2d");

    // Initialize Chart
    const marketChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: [], // Dynamic labels
            datasets: [
                {
                    label: "Bitcoin (BTC)",
                    data: [],
                    segment: {
                        borderColor: (ctx) => {
                            return ctx.p1.parsed.y > ctx.p0.parsed.y
                                ? "#4caf50" // Green for up
                                : "#f44336"; // Red for down
                        },
                    },
                    borderWidth: 2,
                    fill: true,
                    backgroundColor: (ctx) => {
                        const gradient = ctx.chart.ctx.createLinearGradient(0, 0, 0, ctx.chart.height);
                        gradient.addColorStop(0, "rgba(76, 175, 80, 0.2)"); // Green gradient for up
                        gradient.addColorStop(1, "rgba(244, 67, 54, 0.2)"); // Red gradient for down
                        return gradient;
                    },
                },
            ],
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    title: {
                        display: true,
                        text: "Time",
                    },
                },
                y: {
                    title: {
                        display: true,
                        text: "Price (USD)",
                    },
                    ticks: {
                        callback: function (value) {
                            return `$${value}`;
                        },
                    },
                },
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return `${context.dataset.label}: $${context.raw}`;
                        },
                    },
                },
            },
        },
    });

    // Simulate live market data
    setInterval(() => {
        const btcPrice = (48000 + Math.random() * 2000 - 1000).toFixed(2);
        const btcChange = (Math.random() * 5 - 2.5).toFixed(2);

        // Update DOM
        btcPriceElement.textContent = `$${btcPrice}`;
        btcChangeElement.textContent = `${btcChange > 0 ? "+" : ""}${btcChange}%`;
        btcChangeElement.className = `change ${btcChange > 0 ? "up" : "down"}`;

        // Update chart data
        const now = new Date().toLocaleTimeString();
        if (marketChart.data.labels.length > 20) {
            marketChart.data.labels.shift();
            marketChart.data.datasets[0].data.shift();
        }

        marketChart.data.labels.push(now);
        marketChart.data.datasets[0].data.push(parseFloat(btcPrice));

        marketChart.update();
    }, 2000);
});
