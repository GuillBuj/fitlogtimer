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
        // D'abord, on prépare tous les datasets sans filtrer
        const allData = this.chartData.map(d => {
            const date = new Date(d.date);
            return isNaN(date) ? null : {
                x: date.getTime(),
                est1RMmax: d.est1RMmax,
                est1RM3bestAvg: d.est1RM3bestAvg,
                maxWeight: d.maxWeight,
                bodyWeight: d.bodyWeight
            };
        }).filter(d => d != null);

        // On utilise les mêmes dates pour tous les datasets
        const commonDates = allData.map(d => d.x);

        this.est1RMMaxData = allData
            .filter(d => d.est1RMmax != null)
            .map(d => ({ x: d.x, y: d.est1RMmax }));

        this.est1RM3BestData = allData
            .filter(d => d.est1RM3bestAvg != null)
            .map(d => ({ x: d.x, y: d.est1RM3bestAvg }));

        this.maxWeight = allData
            .filter(d => d.maxWeight != null)
            .map(d => ({ x: d.x, y: d.maxWeight }));

        // BodyWeight avec la logique des gaps
        const rawBodyweight = allData
            .filter(d => d.bodyWeight != null && d.bodyWeight > 0)
            .map(d => ({ x: d.x, y: d.bodyWeight }));

        this.bodyWeightData = [];
        for (let i = 0; i < rawBodyweight.length; i++) {
            this.bodyWeightData.push(rawBodyweight[i]);
            if (i < rawBodyweight.length - 1) {
                const diffDays = (rawBodyweight[i + 1].x - rawBodyweight[i].x) / (1000 * 60 * 60 * 24);
                if (diffDays > 30) this.bodyWeightData.push({ x: rawBodyweight[i + 1].x, y: null });
            }
        }

        // Les labels doivent être les dates communes à tous les datasets principaux
        this.labels = commonDates;
    }


    createChart() {
        const canvas = document.getElementById(this.canvasId);
        if (!canvas) { console.error('Canvas not found'); return; }

        const horizontalLinePlugin = {
            id: 'horizontalLinePlugin',
            afterDatasetsDraw(chart) {
                const ctx = chart.ctx;
                const meta = chart.getDatasetMeta(3); // Dataset "Max"
                if (!meta || !meta.data || meta.data.length === 0) return;

                const color = chart.data.datasets[3].borderColor || 'rgba(220,50,50,0.9)';
                ctx.save();

                // Récupère l’échelle X du premier dataset bar (ou par défaut)
                const xScale = chart.scales[Object.keys(chart.scales).find(k => chart.scales[k].axis === 'x')];
                let categoryWidth = 12;

                if (xScale && chart.data.labels.length > 1) {
                    const x0 = xScale.getPixelForTick(0);
                    const x1 = xScale.getPixelForTick(1);
                    categoryWidth = (x1 - x0) * 0.055; // proportion
                }

                // Dessine les segments horizontaux
                meta.data.forEach((point) => {
                    const { x, y } = point.getProps(['x', 'y'], true);
                    if (x == null || y == null) return;

                    ctx.beginPath();
                    ctx.moveTo(x - categoryWidth / 2, y);
                    ctx.lineTo(x + categoryWidth / 2, y);
                    ctx.lineWidth = 4;
                    ctx.strokeStyle = color;
                    ctx.stroke();
                });

                ctx.restore();
            }
        };

        this.chart = new Chart(canvas.getContext('2d'), {
            plugins: [horizontalLinePlugin],
            data: {
                labels: this.labels,
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
                        backgroundColor: 'rgba(60,60,250,0.7)',
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
                    },
                    {
                        type: 'line',
                        label: 'Max',
                        data: this.maxWeight,
                        showLine: false,
                        pointRadius: 0,
                        borderColor: 'rgba(250,200,100,0.9)',
                        borderWidth: 1,
                        backgroundColor: 'transparent',
                        order: 9999
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
        const chart = this.chart; // ← Stocker la référence

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

        const updateChart = (eventTarget = null) => {
            let minPercent = parseInt(sliderMin.value) / 100;
            let maxPercent = parseInt(sliderMax.value) / 100;

            let windowStart = allDates[0] + minPercent * totalRange;
            let windowEnd = allDates[0] + maxPercent * totalRange;

            // Empêcher le chevauchement en respectant 2 mois minimum
            if (windowEnd - windowStart < minWindowMs) {
                if (eventTarget === sliderMin) {
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

            if (chart) {
                chart.options.scales.x.min = windowStart;
                chart.options.scales.x.max = windowEnd;
                chart.update('none');
            }
        };

        // Lier les événements
        sliderMin.addEventListener('input', (e) => {
            updateTrack();
            updateChart(sliderMin);
        });

        sliderMax.addEventListener('input', (e) => {
            updateTrack();
            updateChart(sliderMax);
        });

        // Initialiser sur 2 dernières années
        const twoYearsMs = 2 * 365 * 24 * 60 * 60 * 1000;
        const maxDate = allDates[allDates.length - 1];
        const minDate = Math.max(allDates[0], maxDate - twoYearsMs);

        sliderMin.value = Math.round(((minDate - allDates[0]) / totalRange) * 100);
        sliderMax.value = 100;

        updateTrack();

        // ⚡Appliquer directement la fenêtre avant premier rendu
        if (chart) {
            chart.options.scales.x.min = minDate;
            chart.options.scales.x.max = maxDate;
            chart.update();
        }

        updateChart();
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