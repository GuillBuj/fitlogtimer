document.addEventListener('DOMContentLoaded', () => {

    const percentages = [100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 85, 80, 75, 70, 60, 50];

    const baseValueInput = document.getElementById('baseValue');
    const tableBody = document.getElementById('percentageTableBody');
    const weightInput = document.getElementById('weight');
    const estimatedOneRMInput = document.getElementById('estimatedOneRM');

    function updateTable() {
        const baseValue = parseFloat(baseValueInput.value) || 100;
        tableBody.innerHTML = '';

        percentages.forEach(pct => {
            const value = (baseValue * pct / 100).toFixed(1);
            tableBody.innerHTML += `
                <tr>
                    <td>${pct}%</td>
                    <td>${value}</td>
                    <td class="action-buttons">
                        <button class="send-to-weight" data-value="${value}">➡Poids</button>
                        <button class="send-to-1rm" data-value="${value}">➡1RM</button>
                    </td>
                </tr>
            `;
        });

        // Ajouter les écouteurs d'événements aux nouveaux boutons
        attachEventListeners();
    }

    function attachEventListeners() {
        // Boutons "→ Poids"
        document.querySelectorAll('.send-to-weight').forEach(button => {
            button.addEventListener('click', function() {
                const value = this.getAttribute('data-value');
                if (weightInput) {
                    weightInput.value = value;
                    // Déclencher l'événement input pour mettre à jour les calculs
                    weightInput.dispatchEvent(new Event('input', { bubbles: true }));
                }
            });
        });

        // Boutons "→ 1RM"
        document.querySelectorAll('.send-to-1rm').forEach(button => {
            button.addEventListener('click', function() {
                const value = this.getAttribute('data-value');
                if (estimatedOneRMInput) {
                    estimatedOneRMInput.value = value;
                    // Déclencher l'événement input pour mettre à jour les calculs
                    estimatedOneRMInput.dispatchEvent(new Event('input', { bubbles: true }));
                }
            });
        });
    }

    updateTable();

    baseValueInput.addEventListener('input', updateTable);
    baseValueInput.addEventListener('blur', updateTable);
});