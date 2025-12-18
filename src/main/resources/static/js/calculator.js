document.addEventListener('DOMContentLoaded', () => {
    const weightInput = document.getElementById("weight");
    const repsInput = document.getElementById("reps");
    const estimatedOneRMInput = document.getElementById("estimatedOneRM");
    const rmTableContainer = document.getElementById("rmTableContainer");

    // Stocke les derniers champs modifiés par l'utilisateur
    let lastTwoModified = [];

    // Pour éviter boucle infinie lors mise à jour automatique
    let isUpdatingProgrammatically = false;

    [weightInput, repsInput, estimatedOneRMInput].forEach(input => {
        input.addEventListener('input', (e) => {
            if (isUpdatingProgrammatically) return; // Ignore si mise à jour automatique
            const field = e.target.id;
            lastTwoModified = [...lastTwoModified.filter(id => id !== field), field].slice(-2);
            handleInputChange(field);
        });
    });

    estimatedOneRMInput.addEventListener('input', (e) => {
        if (isUpdatingProgrammatically) return; // Ignore si mise à jour automatique

        const estimatedOneRM = parseFloat(estimatedOneRMInput.value.replace(',', '.'));
        if (!isNaN(estimatedOneRM)) {
            // Si 1RM est valide, mettre à jour le tableau
            generateSuggestedWeightsFrom1RM(estimatedOneRM);
        }
    });

    function handleInputChange(changedField) {
        const weight = parseFloat(weightInput.value.replace(',', '.'));
        const reps = parseFloat(repsInput.value.replace(',', '.'));
        const estimatedOneRM = parseFloat(estimatedOneRMInput.value.replace(',', '.'));

        const isWeightValid = !isNaN(weight) && weight > 0;
        const isRepsValid = !isNaN(reps) && reps > 0;
        const isEstimatedOneRMValid = !isNaN(estimatedOneRM) && estimatedOneRM > 0;

        if (isUpdatingProgrammatically) return;

        // Mise à jour de l'historique des modifications
        lastTwoModified = [...lastTwoModified.filter(id => id !== changedField), changedField].slice(-2);

        isUpdatingProgrammatically = true;

        try {
            // Cas 1: Modification conjointe de reps et 1RM (dans n'importe quel ordre)
            if ((lastTwoModified.includes('reps') && lastTwoModified.includes('estimatedOneRM'))) {
                if (isRepsValid && isEstimatedOneRMValid) {
                    const newWeight = calculateWeight(reps, estimatedOneRM);
                    weightInput.value = newWeight.toFixed(1);
                    highlightField(weightInput);
                    generate1RMTable(newWeight, reps, estimatedOneRM);
                    generateSuggestedWeightsFrom1RM(estimatedOneRM);
                }
            }
            // Cas 2: Modification conjointe de poids et reps
            else if ((lastTwoModified.includes('weight') && lastTwoModified.includes('reps'))) {
                if (isWeightValid && isRepsValid) {
                    const newOneRM = estimate1RM(weight, reps);
                    estimatedOneRMInput.value = newOneRM.toFixed(1);
                    highlightField(estimatedOneRMInput);
                    generate1RMTable(weight, reps, newOneRM);
                    generateSuggestedWeightsFrom1RM(newOneRM);
                }
            }
            // Cas 3: Modification conjointe de poids et 1RM
            else if ((lastTwoModified.includes('weight') && lastTwoModified.includes('estimatedOneRM'))) {
                if (isWeightValid && isEstimatedOneRMValid) {
                    const newReps = calculateReps(weight, estimatedOneRM);
                    repsInput.value = newReps.toFixed(1);
                    highlightField(repsInput);
                    generate1RMTable(weight, newReps, estimatedOneRM);
                    generateSuggestedWeightsFrom1RM(estimatedOneRM);
                }
            }
            // Cas 4: Modification unique du 1RM
            else if (changedField === 'estimatedOneRM' && isEstimatedOneRMValid) {
                generateSuggestedWeightsFrom1RM(estimatedOneRM);
            }
        } catch (error) {
            console.error("Erreur dans handleInputChange:", error);
        } finally {
            isUpdatingProgrammatically = false;
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

    function generate1RMTable(weight, reps, target1RM) {
        const container = document.getElementById("rmTableContainer");
        container.innerHTML = "";

        const nearestReps = Math.round(reps);
        const nearestWeight = Math.round(weight * 2) / 2;

        const repOffsets = [-4, -3, -2, -1, 0, 1, 2, 3, 4];
        const weightOffsets = [-5,-4, -3, -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2, 3, 4, 5];

        // Fonction de coloration relative au 1RM cible
        const getCellStyle = (estimated1RM) => {
            if (!target1RM || target1RM <= 0) return "";

            const ratio = estimated1RM / target1RM;
            const diff = Math.abs(ratio-1);

            let bgColor = '#ffffff'; // blanc par défaut

            if (diff < 0.05) { // ±5% autour de la cible
                const intensity = 1 - (diff / 0.05); // 1 = parfait, 0 = limite de 5%
                const lightness = 94 - (18 * intensity); // de 94% à 76%
                const saturation = 30 + (25 * intensity); // de 30% à 55%

                // hsl(120) = vert. Plus on est proche de la cible, plus c’est saturé et foncé.
                bgColor = `hsl(120, ${saturation}%, ${lightness}%)`;
            }

            const isExact = ratio >= 1.0 && ratio <= 1.006;

            return `
        background-color: ${bgColor};
        ${isExact ? 'font-weight: 700;' : ''}
    `;
        };

        // Filtrer poids > 0
        const validWeightOffsets = weightOffsets.filter(offset => (nearestWeight + offset) > 0);

        let html = `<table class="rm-table"><thead><tr><th></th>`;

        // En-têtes des poids valides uniquement
        validWeightOffsets.forEach(offset => {
            const w = (nearestWeight + offset).toFixed(1);
            html += `<th>${w}</th>`;
        });
        html += `</tr></thead><tbody>`;

        // Corps du tableau, lignes reps > 0 uniquement
        repOffsets.forEach(repOffset => {
            const currentReps = nearestReps + repOffset;
            if (currentReps <= 0) return; // Ignorer reps ≤ 0

            html += `<tr><th class="column-sticky fixed-columns">${currentReps}</th>`;

            validWeightOffsets.forEach(weightOffset => {
                const currentWeight = nearestWeight + weightOffset;
                const estimated1RM = estimate1RM(currentWeight, currentReps);
                const isTargetCell = (repOffset === 0 && weightOffset === 0);

                html += `<td ${isTargetCell ? 'class="target-cell"' : ''}
                    style="${getCellStyle(estimated1RM)}">
                    ${estimated1RM.toFixed(1)}
                </td>`;
            });

            html += `</tr>`;
        });

        html += `</tbody></table>`;
        container.innerHTML = html;
    }

    function generateSuggestedWeightsFrom1RM(oneRM) {
        const container = document.getElementById("suggestedWeightsContainer");
        const repsRange = [...Array.from({length: 14}, (_, i) => i + 2), 20]; // 2 à 15 reps + 20

        // Calcul des données
        const weightsData = repsRange.map(reps => {
            const weight = Math.round(calculateWeight(reps, oneRM) * 2) / 2;
            const estRM = estimate1RM(weight, reps);
            return { reps, weight, estRM };
        });

        // Construction du tableau inversé (colonnes ↔ lignes)
        container.innerHTML = `
    <h3>Poids recommandés pour 1RM = ${oneRM.toFixed(1)} kg</h3>
    <table class="suggested-weights-table rm-table rotated">
        <thead>
            <tr>
                <th>Reps</th>
                ${weightsData.map(data => `<th>${data.reps}</th>`).join('')}
            </tr>
        </thead>
        <tbody>
            <tr>
                <td class="column-sticky">Poids</td>
                ${weightsData.map(data => `<td class="bold-text">${data.weight.toFixed(1)}</td>`).join('')}
            </tr>
            <tr>
                <td class="column-sticky">1RM estimé</td>
                ${weightsData.map(data => `<td>${data.estRM.toFixed(1)}</td>`).join('')}
            </tr>
        </tbody>
    </table>
    `;
    }

    function highlightField(input) {
        input.classList.add("highlight-flash");
        setTimeout(() => {
            input.classList.remove("highlight-flash");
        }, 500);
    }

    // ===== INITIALISATION =====
    function initializeCalculator() {
        const initialReps = parseFloat(repsInput.value) || 10;
        const initialOneRM = parseFloat(estimatedOneRMInput.value) || 74;

        const calculatedWeight = calculateWeight(initialReps, initialOneRM);
        weightInput.value = calculatedWeight.toFixed(1);

        generate1RMTable(calculatedWeight, initialReps, initialOneRM);
        generateSuggestedWeightsFrom1RM(initialOneRM);
    }

    // Lance l'initialisation
    initializeCalculator();
});