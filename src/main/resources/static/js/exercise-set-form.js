function updateExerciseFields() {
    const select = document.getElementById('exercise');
    const selectedOption = select.options[select.selectedIndex];
    const type = selectedOption.getAttribute('data-type');
    console.log("Exercice sélectionné - Type :", type);

    // DOM elements
    const weightGroup = document.getElementById('weightGroup');
    const bandsGroup = document.getElementById('elasticFields');
    const durationGroup = document.getElementById('durationField');

    const weightInput = document.getElementById('weight');
    const bandsInput = document.getElementById('bands');
    const durationInput = document.getElementById('durationS');

    // Reset visibility and required
    weightGroup.style.display = 'none';
    weightInput.required = false;

    bandsGroup.style.display = 'none';
    bandsInput.required = false;

    durationGroup.style.display = 'none';
    durationInput.required = false;

    // Logic based on type
    if (type === 'FREE_WEIGHT') {
        weightGroup.style.display = 'block';
        weightInput.required = true;
    }

    if (type === 'ELASTIC') {
        bandsGroup.style.display = 'block';
        bandsInput.required = true;
    }

    if (type === 'ISOMETRIC') {
        durationGroup.style.display = 'block';
        durationInput.required = true;

        weightGroup.style.display = 'block';
    }

    if (type === 'BODYWEIGHT') {
        weightGroup.style.display = 'block';
    }
}

// Init
window.addEventListener('DOMContentLoaded', updateExerciseFields);
document.getElementById('exercise').addEventListener('change', updateExerciseFields);
