function updateTypeInput() {
    const select = document.getElementById('exercise');
    const selectedOption = select.options[select.selectedIndex];
    const setType = selectedOption.getAttribute('data-type');
    document.getElementById('setTypeInput').value = setType;
}

window.addEventListener('DOMContentLoaded', () => {
    const select = document.getElementById('exercise');
    if (select) {
        select.addEventListener('change', updateTypeInput);
        updateTypeInput();
    }
});