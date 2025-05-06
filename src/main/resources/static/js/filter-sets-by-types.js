document.addEventListener("DOMContentLoaded", function () {
    // Vérifie si on a une liste de checkboxes
    const checkboxes = document.querySelectorAll('input[name="types"]');
    
    if (checkboxes.length > 0) {
        checkboxes.forEach(cb => {
            cb.addEventListener("change", function () {
                const selectedTypes = Array.from(checkboxes)
                    .filter(c => c.checked)
                    .map(c => c.value);

                loadFilteredSets(selectedTypes);
            });
        });
    }

    function loadFilteredSets(selectedTypes) {
        const exerciseId = window.exerciseId || 0;

        console.log("URL:", `/exerciseSets/byExercise/${exerciseId}/groupedByDateClean?types=${selectedTypes.join(',')}`);

        const url = `/exerciseSets/byExercise/${exerciseId}/groupedByDateClean/fragment?` +
            selectedTypes.map(type => `selectedTypes=${encodeURIComponent(type)}`).join('&');

            fetch(url)
            .then(response => response.text())
            .then(data => {
                document.getElementById('filtered-sets').innerHTML = data;
            });
    }

    function clearFilters() {
        const checkboxes = document.querySelectorAll('input[type="checkbox"]');
        checkboxes.forEach(checkbox => checkbox.checked = false);
    
        const selectedTypes = [];
    
        loadFilteredSets(selectedTypes);
    }
});