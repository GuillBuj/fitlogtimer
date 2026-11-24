document.addEventListener('DOMContentLoaded', () => {
    const buttons = document.querySelectorAll('.btn-toggle-compact');
    const tables = document.querySelectorAll('.table-recap');

    if (buttons.length === 0 || tables.length === 0) return;

    // Vue compacte par défaut
    tables.forEach(table => table.classList.add('compact-view'));

    buttons.forEach(button => {
        button.textContent = '+';

        button.addEventListener('click', () => {
            const compact = !tables[0].classList.contains('compact-view');

            tables.forEach(table => {
                table.classList.toggle('compact-view', compact);
            });

            buttons.forEach(btn => {
                btn.textContent = compact ? '+' : '-';
            });
        });
    });
});