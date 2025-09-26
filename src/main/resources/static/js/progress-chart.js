// progress-chart.js
class ProgressChart {
    constructor(chartData, canvasId = 'progressChart') {
        this.chartData = Array.isArray(chartData) ? chartData : [];
        this.canvasId = canvasId;
        this.chart = null;
        this.spanGapsEnabled = false;
        this.maxGapDays = 30;
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
    }

    createChart() {
        const canvas = document.getElementById(this.canvasId);
        if (!canvas) {
            console.error('Canvas element not found:', this.canvasId);
            return null;
        }

        this.chart = new Chart(canvas.getContext('2d'), {
            type: 'line',
            data: {
                datasets: [
                    {
                        label: '1RM Max Estimé',
                        data: this.est1RMMaxData,
                        borderColor: '#ff4444',
                        backgroundColor: 'rgba(255, 68, 68, 0.1)',
                        tension: 0.3,
                        pointRadius: 4,
                        pointHoverRadius: 6,
                        spanGaps: this.spanGapsEnabled,
                    },
                    {
                        label: '1RM 3 Best Avg',
                        data: this.est1RM3BestData,
                        borderColor: '#4444ff',
                        backgroundColor: 'rgba(68, 68, 255, 0.1)',
                        tension: 0.3,
                        pointRadius: 3,
                        pointHoverRadius: 5,
                        spanGaps: this.spanGapsEnabled,
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
                            return `${context.dataset.label}: ${context.parsed.y.toFixed(1)} kg`;
                        }
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
                    }
                },
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: '1RM Estimé (kg)'
                    },
                    ticks: {
                        callback: (value) => value + ' kg'
                    }
                }
            }
        };
    }

    toggleDataset(index) {
        if (!this.chart) return;
        const meta = this.chart.getDatasetMeta(index);
        if (!meta) return;

        meta.hidden = meta.hidden === true ? false : true;
        this.chart.update();
    }

    toggleGaps() {
        if (!this.chart) return;
        this.spanGapsEnabled = !this.spanGapsEnabled;
        this.chart.data.datasets.forEach(dataset => {
            dataset.spanGaps = this.spanGapsEnabled;
        });
        this.chart.update();

        // Mettre à jour le texte du bouton
        const button = document.querySelector('.toggle-gaps-btn');
        if (button) {
            button.textContent = this.spanGapsEnabled ? 'Lignes continues' : 'Lignes cassées';
        }
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

function toggleDataset(index) {
    if (progressChartInstance) {
        progressChartInstance.toggleDataset(index);
    }
}

function toggleGaps() {
    if (progressChartInstance) {
        progressChartInstance.toggleGaps();
    }
}
