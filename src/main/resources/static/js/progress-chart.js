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
    }

    createChart() {
        const canvas = document.getElementById(this.canvasId);
        if (!canvas) {
            console.error('Canvas element not found:', this.canvasId);
            return null;
        }

        this.chart = new Chart(canvas.getContext('2d'), {
            type: 'bar',
            data: {
                datasets: [
                    {
                        label: '1RM 3 Best Avg',
                        data: this.est1RM3BestData,
                        backgroundColor: '#222244', // sombre
                    },
                    {
                        label: '1RM Max Estimé',
                        data: this.est1RMMaxData,
                        backgroundColor: '#6666aa', // moins sombre
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
                    },
                    stacked: false
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
