// Attendre que le DOM soit chargé
document.addEventListener('DOMContentLoaded', function() {
    // Vérifier si chartData est disponible
    if (typeof chartData === 'undefined' || !chartData) {
        console.error('chartData is not defined');
        return;
    }

    // Vérifier si les éléments DOM existent
    const ctx = document.getElementById('periodChart');
    const zoomSlider = document.getElementById('zoomSlider');

    if (!ctx || !zoomSlider) {
        console.error('Required DOM elements not found');
        return;
    }

    // --- VOTRE CODE EXISTANT À PARTIR D'ICI ---
    const exercises = [...new Set(chartData.map(d => d.exerciseName))];
    const periods = [...new Set(chartData.map(d => d.period))].sort();

    const colors = [
        'rgba(54, 162, 235, 0.7)',
        'rgba(153, 102, 255, 0.7)',
        'rgba(255, 206, 86, 0.7)',
        'rgba(75, 192, 192, 0.7)',
        'rgba(255, 99, 132, 0.7)'
    ];

    const datasets = exercises.map((exercise, idx) => {
        const exerciseData = chartData.filter(d => d.exerciseName === exercise);
        return {
            label: exercise,
            data: periods.map(p => {
                const dp = exerciseData.find(d => d.period === p);
                return dp ? dp.max : null;
            }),
            borderColor: colors[idx % colors.length],
            backgroundColor: colors[idx % colors.length],
            tension: 0.3,
            fill: false,
            pointRadius: 3,
            pointHoverRadius: 4
        };
    });

    // Convertir en Date (premier jour du mois ou lundi de la semaine)
    const labelsAsDates = periods.map(p => {
        if (p.includes("W")) { // semaine
            const [year, week] = p.split("-W").map(Number);
            return new Date(year, 0, 1 + (week - 1) * 7);
        } else { // mois
            const [year, month] = p.split("-").map(Number);
            return new Date(year, month - 1, 1);
        }
    }).sort((a, b) => a - b); // Tri des dates

    // Calculer les dates limites
    const minDateOverall = labelsAsDates[0];
    const maxDateOverall = labelsAsDates[labelsAsDates.length - 1];

    // Calculer la durée totale disponible en mois
    const totalMonths = (maxDateOverall.getFullYear() - minDateOverall.getFullYear()) * 12
        + (maxDateOverall.getMonth() - minDateOverall.getMonth()) + 1;

    // Ajuster dynamiquement les limites du slider
    const MIN_ZOOM = 12; // Zoom max : 12 derniers mois
    const MAX_ZOOM = Math.max(totalMonths, MIN_ZOOM); // Dézoom max : toutes les données
    const INITIAL_ZOOM = Math.min(24, MAX_ZOOM); // Initial : 24 mois ou moins

    const chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labelsAsDates,
            datasets: datasets
        },
        options: {
            responsive: true,
            interaction: {
                mode: 'nearest',
                axis: 'x',
                intersect: false
            },
            scales: {
                x: {
                    type: 'time',
                    time: { unit: 'month' },
                    title: { display: true, text: 'Période' }
                },
                y: {
                    beginAtZero: false,
                    title: { display: true, text: 'Poids (kg)' }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: { usePointStyle: true }
                }
            }
        }
    });

    // --- Gestion du slider ---
    function updateZoom(months) {
        const lastDate = maxDateOverall;
        const minDate = new Date(lastDate);
        minDate.setMonth(minDate.getMonth() - months + 1); // +1 pour inclure le mois courant

        // S'assurer qu'on ne dépasse pas la date minimale globale
        const effectiveMinDate = minDate < minDateOverall ? minDateOverall : minDate;

        chart.options.scales.x.min = effectiveMinDate;
        chart.options.scales.x.max = lastDate;
        chart.update();
    }

    // Initialisation dynamique du slider
    zoomSlider.min = MIN_ZOOM;
    zoomSlider.max = MAX_ZOOM;
    zoomSlider.value = INITIAL_ZOOM;

    // fonction pour mettre à jour la couleur du slider
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

    // Init (24 mois par défaut, ou moins si moins de données)
    updateZoom(INITIAL_ZOOM);
    updateSliderColor();

    zoomSlider.addEventListener('input', (e) => {
        const rawValue = parseInt(e.target.value, 10);
        // inversion : plus on va à droite, plus la valeur diminue
        const months = MAX_ZOOM - rawValue + MIN_ZOOM;
        updateZoom(months);
        updateSliderColor();
    });
});