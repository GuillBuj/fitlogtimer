document.addEventListener("DOMContentLoaded", function () {

    let selectedTypes = [];

    const buttons = document.querySelectorAll('.filter-button:not(#clearFilters)');
    const clearButton = document.getElementById('clearFilters');

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            const type = button.textContent.trim();

            if (selectedTypes.includes(type)) {
                selectedTypes = selectedTypes.filter(t => t !== type);
                button.classList.remove('active');
                button.classList.add('inactive');
            } else {
                selectedTypes.push(type);
                button.classList.remove('inactive');
                button.classList.add('active');
            }

            loadFilteredSets(selectedTypes);
        });
    });

    clearButton.addEventListener("click", function () {
        selectedTypes = [];

        buttons.forEach(button => {
            button.classList.remove('active');
            button.classList.add('inactive');
        });

        loadFilteredSets([]); // bien appeler avec une liste vide une seule fois
    });

    function loadFilteredSets(selectedTypes) {
        const exerciseId = window.exerciseId || 0;

        const url = `/exerciseSets/byExercise/${exerciseId}/groupedByDateClean/fragment?` +
            selectedTypes.map(type => `selectedTypes=${encodeURIComponent(type)}`).join('&');

        fetch(url)
            .then(response => response.text())
            .then(data => {
                document.getElementById('filtered-sets').innerHTML = data;
            })
            .catch(error => {
                console.error('Erreur lors du chargement des sets filtrés :', error);
            });
    }
});