// Attendre que le DOM soit prêt
document.addEventListener("DOMContentLoaded", function() {
    // Sélectionner l'élément de filtrage
    const typesSelect = document.getElementById("types-filter");

    // Ajouter un événement pour détecter les changements dans les options sélectionnées
    typesSelect.addEventListener("change", function() {
        // Récupérer les types sélectionnés
        const selectedTypes = Array.from(typesSelect.selectedOptions)
            .map(option => option.value);

        // Appeler la fonction AJAX pour charger les sets filtrés
        loadFilteredSets(selectedTypes);
    });

    // Fonction AJAX pour récupérer les sets filtrés
    function loadFilteredSets(selectedTypes) {
        
        const exerciseId = /* récupérer l'ID de l'exercice de façon dynamique, par ex. via une variable JavaScript */;
        
        // Appeler le contrôleur avec AJAX
        fetch(`/exerciseSets/byExercise/${exerciseId}/groupedByDateClean?types=${selectedTypes.join(',')}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.text()) // Utilise `.json()` si tu renvoies un JSON, `.text()` si c'est du HTML
        .then(data => {
            // Mettre à jour le contenu avec la réponse
            document.getElementById('filtered-sets').innerHTML = data;
        })
        .catch(error => {
            console.error('Error fetching filtered sets:', error);
        });
    }
});