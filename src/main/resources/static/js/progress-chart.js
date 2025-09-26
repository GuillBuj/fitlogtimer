class ProgressChart {
    constructor(chartData, canvasId = 'progressChart') {
        this.chartData = Array.isArray(chartData) ? chartData : [];
        this.canvasId = canvasId;
        this.chart = null;
        this.init();
    }

    init() {
        this.prepareData();
        this.createChart();
    }

    prepareData() {
        this.est1RMMaxData = this.chartData
            .map(item => {
                const date = new Date(item.date);
                return isNaN(date) ? null : { x: date.getTime(), y: item.est1RMmax };
            })
            .filter(item => item && item.y != null);

        this.est1RM3BestData = this.chartData
            .map(item => {
                const date = new Date(item.date);
                return isNaN(date) ? null : { x: date.getTime(), y: item.est1RM3bestAvg };
            })
            .filter(item => item && item.y != null);

        // 🔹 Bodyweight : on filtre les 0 et on "casse" quand > 30 jours
        const rawBodyweight = this.chartData
            .map(item => {
                const date = new Date(item.date);
                return isNaN(date) || !item.bodyWeight || item.bodyWeight <= 0
                    ? null
                    : { x: date.getTime(), y: item.bodyWeight };
            })
            .filter(item => item);

        this.bodyWeightData = [];
        for (let i = 0; i < rawBodyweight.length; i++) {
            this.bodyWeightData.push(rawBodyweight[i]);

            if (i < rawBodyweight.length - 1) {
                const diffDays = (rawBodyweight[i + 1].x - rawBodyweight[i].x) / (1000 * 60 * 60 * 24);
                if (diffDays > 30) {
                    // 🔹 ajoute un "trou" → force Chart.js à couper la ligne
                    this.bodyWeightData.push({ x: rawBodyweight[i + 1].x, y: null });
                }
            }
        }
    }

    createChart() {
        const canvas = document.getElementById(this.canvasId);
        if (!canvas) {
            console.error('Canvas element not found:', this.canvasId);
            return null;
        }

        this.chart = new Chart(canvas.getContext('2d'), {
            data: {
                datasets: [
                    {
                        type: 'bar',
                        label: '1RM 3 Best Avg',
                        data: this.est1RM3BestData,
                        backgroundColor: 'rgba(34, 34, 68, 0.7)',
                        barPercentage: 0.7,
                        categoryPercentage: 1
                    },
                    {
                        type: 'bar',
                        label: '1RM Max Estimé',
                        data: this.est1RMMaxData,
                        backgroundColor: 'rgba(102, 102, 170, 0.7)',
                        barPercentage: 0.7,
                        categoryPercentage: 1
                    },
                    {
                        type: 'line',
                        label: 'Poids du corps',
                        data: this.bodyWeightData,
                        borderColor: 'rgba(250, 128, 114, 0.9)',
                        backgroundColor: 'transparent',
                        borderWidth: 0.6,
                        spanGaps: false,
                        yAxisID: 'y',
                        tension: 0.3,
                        pointRadius: 0,
                        pointHoverRadius: 1,
                    }
                ]
            },
            options: this.getChartOptions()
        });

        return this.chart;
    }

    getChartOptions() {
        return {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'nearest',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                },
                tooltip: {
                    callbacks: {
                        title: (context) => {
                            if (!context || !context[0]) return '';
                            return new Date(context[0].parsed.x).toLocaleDateString('fr-FR');
                        },
                        label: (context) => {
                            return `${context.dataset.label}: ${context.parsed.y?.toFixed(1) ?? '-'} kg`;
                        }
                    }
                },
                zoom: {
                    pan: {
                        enabled: true,
                        mode: 'x',
                    },
                    zoom: {
                        wheel: {
                            enabled: true,
                        },
                        pinch: {
                            enabled: true,
                        },
                        mode: 'x',
                    }
                }
            },
            scales: {
                x: {
                    type: 'time',
                    time: {
                        unit: 'month',
                        tooltipFormat: 'dd/MM/yyyy'
                    },
                    title: {
                        display: true,
                        text: 'Date'
                    },
                    stacked: true   // <-- clé pour forcer superposition au même abscisse
                },
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: 'Valeurs (kg)'
                    },
                    ticks: {
                        callback: (value) => value + ' kg'
                    }
                }
            }
        };
    }

    destroy() {
        if (this.chart) {
            this.chart.destroy();
            this.chart = null;
        }
    }
}

// Instance globale
let progressChartInstance = null;

function initProgressChart(chartData, canvasId = 'progressChart') {
    if (progressChartInstance) {
        progressChartInstance.destroy();
    }
    progressChartInstance = new ProgressChart(chartData, canvasId);
    return progressChartInstance;
}
