function setUpFamilyFilter(){
    const familyFilter = document.getElementById('familyFilter');
    const muscleFilter = document.getElementById('muscleFilter');
    const typeFilter = document.getElementById('typeFilter');
    const rows = document.querySelectorAll('tbody tr[data-family]');

    if (!familyFilter && !muscleFilter) return;

    function applyFilters(){
        const selectedFamily = familyFilter?.value || 'all';
        const selectedMuscle = muscleFilter?.value || 'all';
        const selectedType = typeFilter?.value || 'all';

        rows.forEach(row=> {
            const rowFamily = row.getAttribute('data-family');
            const rowMuscle = row.getAttribute('data-muscle');
            const rowType = row.getAttribute('data-type');

            const familyMatch = selectedFamily === 'all' || rowFamily === selectedFamily;
            const muscleMatch = selectedMuscle === 'all' || rowMuscle === selectedMuscle;
            const typeMatch = selectedType === 'all' || rowType === selectedType;
            row.style.display =(familyMatch && muscleMatch && typeMatch)?
                '' : 'none';
        });
    }

    if(familyFilter){
        familyFilter.addEventListener('change', applyFilters);
    }
    if(muscleFilter){
        muscleFilter.addEventListener('change', applyFilters)
    }
    if(typeFilter){
        typeFilter.addEventListener('change', applyFilters)
    }
}

document.addEventListener('DOMContentLoaded', setUpFamilyFilter)
//à decom si ajax
//document.body.addEventListener('htmx:afterSwap', setUpFamilyFilter)