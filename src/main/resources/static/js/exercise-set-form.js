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
    const setModeGroup = document.getElementById('setModeField');

    const repsInput = document.getElementById('repNumber');
    const weightInput = document.getElementById('weight');
    const bandsInput = document.getElementById('bands');
    const durationInput = document.getElementById('durationS');
    const distanceInput = document.getElementById('distance');
    const setModeInput = document.getElementById('setMode');
    const setMode = setModeInput.value;

    // Reset visibility and required
    weightGroup.style.display = 'none';
    weightInput.required = false;

    bandsGroup.style.display = 'none';
    bandsInput.required = false;

    durationGroup.style.display = 'none';
    durationInput.required = false;

    distanceGroup.style.display = 'none';
    distanceInput.required = false;

    setModeGroup.style.display = 'none';
    setModeInput.required = false;

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
        setModeGroup.style.display = 'block';
console.log("Mode :", setModeInput.value);
        if (setMode === 'TIME_TRIAL') {
            durationGroup.style.display = 'block';
            durationInput.required = true;

            weightGroup.style.display = 'none';
            weightInput.required = false;
        } else {
            weightGroup.style.display = 'block';
            durationGroup.style.display = 'none';
            durationInput.required = false;
        }
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
document.getElementById('setMode').addEventListener('change', updateExerciseFields);
