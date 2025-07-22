document.addEventListener('DOMContentLoaded', () => {
    const list = document.getElementById('exercise-list');
    Sortable.create(list, {
        animation: 150
    });
});