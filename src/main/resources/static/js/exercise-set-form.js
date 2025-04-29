function updateExerciseFields() {
    const select = document.getElementById('exercise');
    const selectedOption = select.options[select.selectedIndex];
    const type = selectedOption.getAttribute('data-type');
    console.log("Exercice sélectionné - Type :", type);

    // DOM elements
    const weightGroup = document.getElementById('weightField');
    const bandsGroup = document.getElementById('elasticField');
    const durationGroup = document.getElementById('durationField');
    const distanceGroup = document.getElementById('distanceField');

    const repsInput = document.getElementById('repNumber');
    const weightInput = document.getElementById('weight');
    const bandsInput = document.getElementById('bands');
    const durationInput = document.getElementById('durationS');
    const distanceInput = document.getElementById('distance');

    // Reset visibility and required
    weightGroup.style.display = 'none';
    weightInput.required = false;

    bandsGroup.style.display = 'none';
    bandsInput.required = false;

    durationGroup.style.display = 'none';
    durationInput.required = false;

    distanceGroup.style.display = 'none';
    distanceInput.required = false;

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

    if (type === 'MOVEMENT') {
        bandsGroup.style.display = 'block';
        
        distanceGroup.style.display = 'block';
        distanceInput.required = true;

        weightGroup.style.display = 'block';
    }
}

// Init
window.addEventListener('DOMContentLoaded', updateExerciseFields);
document.getElementById('exercise').addEventListener('change', updateExerciseFields);
