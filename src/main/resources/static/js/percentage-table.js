document.addEventListener('DOMContentLoaded', () => {

    const percentages = [100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 85, 80, 75, 70, 65, 60, 55, 50];

    const baseValueInput = document.getElementById('baseValue');
    const tableBody = document.getElementById('percentageTableBody');

    function updateTable() {
        const baseValue = parseFloat(baseValueInput.value) || 100;
        tableBody.innerHTML = '';

        percentages.forEach(pct => {
            const value = (baseValue * pct / 100).toFixed(1);
            tableBody.innerHTML += `
                <tr>
                    <td>${pct}%</td>
                    <td>${value}</td>
                </tr>
            `;
        });
    }

    updateTable();

    baseValueInput.addEventListener('input', updateTable);

    baseValueInput.addEventListener('blur', updateTable);
});