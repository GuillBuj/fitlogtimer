function setUpFamilyFilter(){
    const familyFilter = document.getElementById('familyFilter');
    const muscleFilter = document.getElementById('muscleFilter');
    const typeFilter = document.getElementById('typeFilter');
    const shortSearch = document.getElementById('shortSearch');
    const nameSearch = document.getElementById('nameSearch');

    const rows = document.querySelectorAll('tbody tr[data-family]');

    if (!familyFilter && !muscleFilter && !typeFilter &&
        !shortSearch && !nameSearch) return;

    function applyFilters(){
        const selectedFamily = familyFilter?.value || 'all';
        const selectedMuscle = muscleFilter?.value || 'all';
        const selectedType = typeFilter?.value || 'all';

        const searchedShort = shortSearch?.value.trim().toLowerCase() || '';
        const searchedName = nameSearch?.value.trim().toLowerCase() || '';

        rows.forEach(row => {
            const rowFamily = row.getAttribute('data-family');
            const rowMuscle = row.getAttribute('data-muscle');
            const rowType = row.getAttribute('data-type');

            const rowShort = row.cells[0].textContent.trim().toLowerCase();
            const rowName = row.cells[2].textContent.trim().toLowerCase();

            const familyMatch = selectedFamily === 'all' || rowFamily === selectedFamily;
            const muscleMatch = selectedMuscle === 'all' || rowMuscle === selectedMuscle;
            const typeMatch = selectedType === 'all' || rowType === selectedType;

            const shortMatch = rowShort.includes(searchedShort);
            const nameMatch = rowName.includes(searchedName);

            row.style.display =
                familyMatch && muscleMatch && typeMatch &&
                shortMatch && nameMatch ? '' : 'none';
        });
    }

    [familyFilter, muscleFilter, typeFilter].forEach(filter => {
        filter?.addEventListener('change', applyFilters);
    });

    [shortSearch, nameSearch].forEach(search => {
        search?.addEventListener('input', applyFilters);
    });
}

document.addEventListener('DOMContentLoaded', setUpFamilyFilter);