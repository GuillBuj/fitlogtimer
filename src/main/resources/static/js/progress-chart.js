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
        this.setupSlider();
    }

    prepareData() {
        this.est1RMMaxData = this.chartData.map(d => {
            const date = new Date(d.date);
            return isNaN(date) ? null : { x: date.getTime(), y: d.est1RMmax };
        }).filter(d => d && d.y != null);

        this.est1RM3BestData = this.chartData.map(d => {
            const date = new Date(d.date);
            return isNaN(date) ? null : { x: date.getTime(), y: d.est1RM3bestAvg };
        }).filter(d => d && d.y != null);

        const rawBodyweight = this.chartData.map(d => {
            const date = new Date(d.date);
            return isNaN(date) || !d.bodyWeight || d.bodyWeight <= 0 ? null : { x: date.getTime(), y: d.bodyWeight };
        }).filter(d => d);

        this.bodyWeightData = [];
        for (let i = 0; i < rawBodyweight.length; i++) {
            this.bodyWeightData.push(rawBodyweight[i]);
            if (i < rawBodyweight.length - 1) {
                const diffDays = (rawBodyweight[i + 1].x - rawBodyweight[i].x)/(1000*60*60*24);
                if (diffDays > 30) this.bodyWeightData.push({ x: rawBodyweight[i + 1].x, y: null });
            }
        }
    }

    createChart() {
        const canvas = document.getElementById(this.canvasId);
        if (!canvas) { console.error('Canvas not found'); return; }

        this.chart = new Chart(canvas.getContext('2d'), {
            data: {
                datasets: [
                    {
                        type: 'bar',
                        label: '1RM 3 Best Avg',
                        data: this.est1RM3BestData,
                        backgroundColor: 'rgba(10,10,50,0.7)',
                        barPercentage: 0.8,
                        categoryPercentage: 1
                    },
                    {
                        type: 'bar',
                        label: '1RM Max Estimé',
                        data: this.est1RMMaxData,
                        backgroundColor: 'rgba(60,60,250,0.9)',
                        barPercentage: 0.8,
                        categoryPercentage: 1
                    },
                    {
                        type: 'line',
                        label: 'Poids du corps',
                        data: this.bodyWeightData,
                        borderColor: 'rgba(250,128,114,0.9)',
                        backgroundColor: 'transparent',
                        borderWidth: 1,
                        spanGaps: false,
                        tension: 0.3,
                        pointRadius: 0,
                        pointHoverRadius: 1
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: { mode: 'nearest', intersect: false },
                plugins: {
                    legend: { display: true, position: 'top' },
                    tooltip: {
                        callbacks: {
                            title: ctx => ctx[0] ? new Date(ctx[0].parsed.x).toLocaleDateString('fr-FR') : '',
                            label: ctx => `${ctx.dataset.label}: ${ctx.parsed.y?.toFixed(1) ?? '-'} kg`
                        }
                    }
                },
                scales: {
                    x: {
                        type: 'time',
                        stacked: true,
                        time: { unit: 'month', tooltipFormat: 'dd/MM/yyyy' },
                        title: { display: true, text: 'Date' }
                    },
                    y: {
                        beginAtZero: false,
                        title: { display: true, text: 'Valeurs (kg)' },
                        ticks: { callback: v => v + ' kg' }
                    }
                }
            }
        });

        return this.chart;
    }

    setupSlider() {
        if (this.chartData.length === 0) return;

        const sliderMin = document.getElementById('sliderMin');
        const sliderMax = document.getElementById('sliderMax');
        const track = document.querySelector('.slider-track');

        const allDates = this.chartData.map(d => new Date(d.date).getTime()).sort((a, b) => a - b);
        const totalRange = allDates[allDates.length - 1] - allDates[0];

        // Minimum 2 mois en millisecondes
        const minWindowMs = 2 * 30 * 24 * 60 * 60 * 1000;

        function updateTrack() {
            const minVal = parseInt(sliderMin.value);
            const maxVal = parseInt(sliderMax.value);

            track.style.setProperty('--track-start', minVal + '%');
            track.style.setProperty('--track-width', (maxVal - minVal) + '%');
        }

        function updateChart() {
            let minPercent = parseInt(sliderMin.value) / 100;
            let maxPercent = parseInt(sliderMax.value) / 100;

            let windowStart = allDates[0] + minPercent * totalRange;
            let windowEnd = allDates[0] + maxPercent * totalRange;

            // Empêcher le chevauchement en respectant 2 mois minimum
            if (windowEnd - windowStart < minWindowMs) {
                if (this.eventTarget === sliderMin) {
                    // L’utilisateur déplace le min
                    windowStart = windowEnd - minWindowMs;
                    minPercent = (windowStart - allDates[0]) / totalRange;
                    sliderMin.value = Math.round(minPercent * 100);
                } else {
                    // L’utilisateur déplace le max
                    windowEnd = windowStart + minWindowMs;
                    maxPercent = (windowEnd - allDates[0]) / totalRange;
                    sliderMax.value = Math.round(maxPercent * 100);
                }
            }

            if (this.chart) {
                this.chart.options.scales.x.min = windowStart;
                this.chart.options.scales.x.max = windowEnd;
                this.chart.update('none');
            }
        }

        // Lier les événements
        sliderMin.addEventListener('input', (e) => {
            updateTrack();
            updateChart.call({ chart: this.chart, eventTarget: sliderMin });
        });

        sliderMax.addEventListener('input', (e) => {
            updateTrack();
            updateChart.call({ chart: this.chart, eventTarget: sliderMax });
        });

        // Initialiser sur 2 dernières années
        const twoYearsMs = 2 * 365 * 24 * 60 * 60 * 1000;
        const maxDate = allDates[allDates.length - 1];
        const minDate = Math.max(allDates[0], maxDate - twoYearsMs);

        sliderMin.value = Math.round(((minDate - allDates[0]) / totalRange) * 100);
        sliderMax.value = 100;

        updateTrack();
        updateChart.call({ chart: this.chart, eventTarget: sliderMax });
    }


    destroy() {
        if (this.chart) {
            this.chart.destroy();
            this.chart = null;
        }
    }
}

let progressChartInstance = null;

function initProgressChart(chartData, canvasId = 'progressChart') {
    if (progressChartInstance) progressChartInstance.destroy();
    progressChartInstance = new ProgressChart(chartData, canvasId);
    return progressChartInstance;
}