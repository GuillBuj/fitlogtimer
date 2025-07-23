document.addEventListener('DOMContentLoaded', () => {
    const weightInput = document.getElementById("weight");
    const repsInput = document.getElementById("reps");
    const estimatedOneRMInput = document.getElementById("estimatedOneRM");
    const rmTableContainer = document.getElementById("rmTableContainer");

    // Stocke le dernier champ modifié par l'utilisateur
    let lastManuallyEdited = null;

    // Pour éviter boucle infinie lors mise à jour automatique
    let isUpdatingProgrammatically = false;

    [weightInput, repsInput, estimatedOneRMInput].forEach(input => {
        input.addEventListener('input', (e) => {
            if (isUpdatingProgrammatically) return; // Ignore si mise à jour automatique
            lastManuallyEdited = e.target.id;
            handleInputChange();
        });
    });

    function handleInputChange() {
        const weight = parseFloat(weightInput.value.replace(',', '.'));
        const reps = parseFloat(repsInput.value.replace(',', '.'));
        const estimatedOneRM = parseFloat(estimatedOneRMInput.value.replace(',', '.'));

        const isWeightValid = !isNaN(weight);
        const isRepsValid = !isNaN(reps);
        const isEstimatedOneRMValid = !isNaN(estimatedOneRM);

        if (isWeightValid && isRepsValid && isEstimatedOneRMValid) {
            isUpdatingProgrammatically = true;

            if (lastManuallyEdited === 'weight') {
                const newReps = calculateReps(weight, estimatedOneRM);
                repsInput.value = newReps.toFixed(1);
                const newOneRM = estimate1RM(weight, newReps);
                estimatedOneRMInput.value = newOneRM.toFixed(1);
            } else if (lastManuallyEdited === 'reps') {
                const newWeight = calculateWeight(reps, estimatedOneRM);
                weightInput.value = newWeight.toFixed(1);
                const newOneRM = estimate1RM(newWeight, reps);
                estimatedOneRMInput.value = newOneRM.toFixed(1);
            } else if (lastManuallyEdited === 'estimatedOneRM') {
                const newWeight = calculateWeight(reps, estimatedOneRM);
                weightInput.value = newWeight.toFixed(1);
                const newReps = calculateReps(newWeight, estimatedOneRM);
                repsInput.value = newReps.toFixed(1);
            }

            isUpdatingProgrammatically = false;

            generate1RMTable(parseFloat(weightInput.value), parseFloat(repsInput.value));
            return;
        }

        if (isWeightValid && isRepsValid && !isEstimatedOneRMValid) {
            const calculatedOneRM = estimate1RM(weight, reps);
            isUpdatingProgrammatically = true;
            estimatedOneRMInput.value = calculatedOneRM.toFixed(1);
            isUpdatingProgrammatically = false;

            generate1RMTable(weight, reps);
            return;
        }

        if (isEstimatedOneRMValid && isRepsValid && !isWeightValid) {
            const calculatedWeight = calculateWeight(reps, estimatedOneRM);
            isUpdatingProgrammatically = true;
            weightInput.value = calculatedWeight.toFixed(1);
            isUpdatingProgrammatically = false;

            generate1RMTable(calculatedWeight, reps);
            return;
        }

        if (isEstimatedOneRMValid && isWeightValid && !isRepsValid) {
            const calculatedReps = calculateReps(weight, estimatedOneRM);
            isUpdatingProgrammatically = true;
            repsInput.value = calculatedReps.toFixed(1);
            isUpdatingProgrammatically = false;

            generate1RMTable(weight, calculatedReps);
            return;
        }

    }

    function estimate1RM(weight, reps) {
        return (reps < 10)
            ? (weight / (1.0278 - 0.0278 * reps) + weight * Math.pow(reps, 0.1)) / 2
            : (weight * (1 + 0.0333 * reps) + weight * Math.pow(reps, 0.1)) / 2;
    }

    function calculateWeight(reps, oneRM) {
        if (reps < 10) {
            return 2 * oneRM / (1 / (1.0278 - 0.0278 * reps) + Math.pow(reps, 0.1));
        } else {
            return 2 * oneRM / ((1 + 0.0333 * reps) + Math.pow(reps, 0.1));
        }
    }

    function calculateReps(weight, oneRM) {
        let bestReps = 1;
        let minError = Infinity;

        for (let r = 1; r <= 30; r += 0.1) {
            const est = estimate1RM(weight, r);
            const err = Math.abs(est - oneRM);
            if (err < minError) {
                minError = err;
                bestReps = r;
            }
        }

        return bestReps;
    }

    function generate1RMTable(weight, reps) {
        const container = document.getElementById("rmTableContainer");
        container.innerHTML = "";

        const nearestReps = Math.round(reps);
        const nearestWeight = Math.round(weight * 2) / 2;

        const repOffsets = [-2, -1, 0, 1, 2];
        const weightOffsets = [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2];

        // Filtrer poids > 0
        const validWeightOffsets = weightOffsets.filter(offset => (nearestWeight + offset) > 0);

        let html = `<h4>Estimations autour de ${nearestReps} reps & ${nearestWeight} kg :</h4>`;
        html += `<table class="rm-table"><thead><tr><th></th>`;

        // En-têtes des poids valides uniquement
        validWeightOffsets.forEach(offset => {
            const w = (nearestWeight + offset).toFixed(1);
            html += `<th>${w} kg</th>`;
        });
        html += `</tr></thead><tbody>`;

        // Corps du tableau, lignes reps > 0 uniquement
        repOffsets.forEach(repOffset => {
            const currentReps = nearestReps + repOffset;
            if (currentReps <= 0) return; // Ignorer reps ≤ 0

            html += `<tr><th>${currentReps} reps</th>`;

            validWeightOffsets.forEach(weightOffset => {
                const currentWeight = nearestWeight + weightOffset;
                const estimated1RM = estimate1RM(currentWeight, currentReps);
                const isTarget = (repOffset === 0 && weightOffset === 0);
                html += `<td${isTarget ? ' class="target-cell"' : ''}>${estimated1RM.toFixed(1)} kg</td>`;
            });

            html += `</tr>`;
        });

        html += `</tbody></table>`;
        container.innerHTML = html;
    }

});