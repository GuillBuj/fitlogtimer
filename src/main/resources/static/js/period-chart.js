document.addEventListener('DOMContentLoaded', function() {
    if (typeof chartData === 'undefined' || !chartData) {
        console.error('chartData is not defined');
        return;
    }

    const ctx = document.getElementById('periodChart');
    const zoomSlider = document.getElementById('zoomSlider');

    if (!ctx || !zoomSlider) {
        console.error('Required DOM elements not found');
        return;
    }

    // --- Fonction pour parser period -> Date ---
    function parsePeriod(p) {
        if (p.includes("W")) {
            const [yearStr, weekStr] = p.split("-W");
            const year = +yearStr;
            const week = +weekStr;

            const jan4 = new Date(Date.UTC(year, 0, 4));
            const dayOfWeek = jan4.getUTCDay() || 7;
            const mondayOfWeek1 = new Date(jan4);
            mondayOfWeek1.setUTCDate(jan4.getUTCDate() - (dayOfWeek - 1));
            const result = new Date(mondayOfWeek1);
            result.setUTCDate(mondayOfWeek1.getUTCDate() + (week - 1) * 7);
            return result;
        } else {
            const [year, month] = p.split("-").map(Number);
            return new Date(Date.UTC(year, month - 1, 1));
        }
    }

    // --- Couleurs par exercice ---
    const colors1 = [

        'rgba(54, 162, 235, 0.7)',    // Bleu
         'rgba(153, 102, 255, 0.7)',   // Violet
         'rgba(52, 73, 94, 0.7)',      // Bleu acier
         'rgba(255, 206, 86, 0.7)',    // Jaune
         'rgba(46, 204, 113, 0.7)',    // Émeraude
         'rgba(75, 192, 192, 0.7)',    // Turquoise
         'rgba(231, 76, 60, 0.7)',     // Rouge tomate
         'rgba(255, 99, 132, 0.7)',    // Rouge
         'rgba(255, 159, 64, 0.7)',    // Orange
         'rgba(201, 203, 207, 0.7)',   // Gris
         'rgba(102, 204, 102, 0.7)',   // Vert
         'rgba(220, 100, 220, 0.7)',   // Rose
         'rgba(255, 102, 102, 0.7)',   // Rouge clair
         'rgba(155, 89, 182, 0.7)',    // Violet foncé
         'rgba(241, 196, 15, 0.7)',    // Or
         'rgba(230, 126, 34, 0.7)',    // Carotte
         'rgba(149, 165, 166, 0.7)',   // Gris béton
         'rgba(26, 188, 156, 0.7)',    // Turquoise
         'rgba(142, 68, 173, 0.7)'     // Violet intense
     ];
    const colors = [
        'rgba(28, 81, 117, 0.7)',     // Bleu foncé (au lieu de 54,162,235)
        'rgba(61, 122, 61, 0.7)',     // Vert forêt
        'rgba(93, 53, 109, 0.7)',     // Aubergine
        'rgba(173, 57, 45, 0.7)',     // Rouge rouille
        'rgba(120, 121, 124, 0.7)',   // Gris anthracite
        'rgba(191, 119, 48, 0.7)',    // Orange foncé
        'rgba(191, 74, 99, 0.7)',     // Rouge bordeaux (au lieu de 255,99,132)
        'rgba(37, 96, 96, 0.7)',      // Turquoise foncé (au lieu de 75,192,192)
        'rgba(191, 154, 64, 0.7)',    // Jaune doré foncé (au lieu de 255,206,86)
        'rgba(76, 51, 127, 0.7)',     // Violet foncé (au lieu de 153,102,255)
        'rgba(191, 76, 76, 0.7)',     // Rouge foncé
        'rgba(31, 91, 131, 0.7)',     // Bleu marine
        'rgba(165, 75, 165, 0.7)',    // Violet prune
        'rgba(180, 147, 11, 0.7)',    // Or vieilli
        'rgba(23, 102, 56, 0.7)',     // Vert sapin

    ];

    // --- Périodes globales pour gérer les gaps ---
    const periods = [...new Set(chartData.map(d => d.period))].sort();

    // --- Regrouper les données par exercice ---
    const exercises = [...new Set(chartData.map(d => d.exerciseName))];

    const datasets = exercises
        .filter(exercise => exercise !== "_GHOST_") // on ignore carrément les ghost
        .map((exercise, idx) => {
            const exerciseData = periods.map(p => {
                const dp = chartData.find(d => d.exerciseName === exercise && d.period === p);
                return {
                    x: parsePeriod(p),
                    y: dp ? dp.max : null,   // ⚡ null au lieu de NaN
                    recordType: dp ? dp.recordType : null
                };
            });

            return {
                label: exercise,
                data: exerciseData,
                borderColor: colors[idx % colors.length],
                backgroundColor: colors[idx % colors.length],
                tension: 0.3,
                fill: false,
                spanGaps: false,   // ⚡ garde les trous quand c’est null
            };
        });



    // --- min/max pour slider ---
    const allDates = datasets.flatMap(ds => ds.data.map(p => p.x)).sort((a,b) => a-b);
    const minDateOverall = allDates[0];
    const maxDateOverall = allDates[allDates.length - 1];

    const totalMonths = (maxDateOverall.getFullYear() - minDateOverall.getFullYear()) * 12
        + (maxDateOverall.getMonth() - minDateOverall.getMonth()) + 1;
    const MIN_ZOOM = 12;
    const MAX_ZOOM = Math.max(totalMonths, MIN_ZOOM);
    const INITIAL_ZOOM = Math.min(24, MAX_ZOOM);

    // --- Chart ---
    const chart = new Chart(ctx, {
        type: 'line',
        data: { datasets },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'nearest', axis: 'x', intersect: false },
            scales: {
                x: { type: 'time', time: { unit: 'month' }, title: { display: true, text: 'Période' }, grid: { display: true } },
                y: { beginAtZero: false, title: { display: true, text: 'Poids (kg)' }, grid: { display: true } }
            },
            transitions: {
                active: {
                    animation: {
                        duration: 0
                    }
                },
                hide: {
                    animation: {
                        duration: 0 // Désactive l'animation de masquage
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: {
                        usePointStyle: true
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const y = context.raw?.y;
                            if (y == null) return '';
                            return `${context.dataset.label}: ${y} kg`;
                        },
                        title: function(context) {
                            const date = context[0].parsed.x; // date en ms
                            const d = new Date(date);
                            const month = d.getUTCMonth() + 1;
                            const day = d.getUTCDate();
                            const year = d.getUTCFullYear();
                            return `${day.toString().padStart(2,'0')}/${month.toString().padStart(2,'0')}/${year}`;
                        }
                    }
                }

            },
            elements: {
                point: {
                    radius: (ctx) => ctx.dataset.label === "_GHOST_" ? 0 : 3,
                    backgroundColor: (ctx) => ctx.raw?.recordType === 'SB' ? '#1E90FF' : ctx.dataset.borderColor,
                    borderWidth: 1,
                    borderColor: '#fff'
                }
            }
        },
        plugins: [
            {
                id: 'prHalo',
                beforeDatasetsDraw(chart) {
                    const { ctx } = chart;
                    chart.data.datasets.forEach((dataset, datasetIndex) => {
                        const meta = chart.getDatasetMeta(datasetIndex);

                        if (!meta.visible) return;

                        meta.data.forEach((point, index) => {
                            const dp = dataset.data[index];
                            if (!dp || isNaN(dp.y)) return;

                            if (dp.recordType === 'PR') {
                                ctx.save();
                                ctx.beginPath();
                                ctx.arc(point.x, point.y, 8, 0, 2 * Math.PI);
                                ctx.fillStyle = 'rgba(255, 180, 0, 0.7)';
                                ctx.fill();
                                ctx.restore();
                            }
                            if (dp.recordType === 'SB') {
                                ctx.save();
                                ctx.beginPath();
                                ctx.arc(point.x, point.y, 5.5, 0, 2 * Math.PI);
                                ctx.fillStyle = 'rgba(200, 165, 0, 0.5)';
                                ctx.fill();
                                ctx.restore();
                            }
                        });
                    });
                }
            }
        ]
    });

    // --- Slider avec padding dynamique ---
    function updateZoom(months) {
        const lastDate = maxDateOverall;
        const minDate = new Date(lastDate);
        minDate.setMonth(minDate.getMonth() - months + 1);
        const effectiveMinDate = minDate < minDateOverall ? minDateOverall : minDate;

        const range = lastDate - effectiveMinDate;
        const paddedMaxDate = new Date(lastDate.getTime() + range * 0.05);

        chart.options.scales.x.min = effectiveMinDate;
        chart.options.scales.x.max = paddedMaxDate;
        chart.update();
    }

    function updateSliderColor() {
        const min = parseInt(zoomSlider.min, 10);
        const max = parseInt(zoomSlider.max, 10);
        const val = ((zoomSlider.value - min) * 100) / (max - min);
        zoomSlider.style.background = `linear-gradient(
            to right,
            #eeeeee 0%,
            #eeeeee ${val}%,
            #666666 ${val}%,
            #666666 100%
        )`;
    }

    zoomSlider.min = MIN_ZOOM;
    zoomSlider.max = MAX_ZOOM;
    zoomSlider.value = INITIAL_ZOOM;

    updateZoom(INITIAL_ZOOM);
    updateSliderColor();

    zoomSlider.addEventListener('input', (e) => {
        const rawValue = parseInt(e.target.value, 10);
        const months = MAX_ZOOM - rawValue + MIN_ZOOM;
        updateZoom(months);
        updateSliderColor();
    });
});
