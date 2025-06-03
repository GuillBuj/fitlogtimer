function setUpFamilyFilter(){
    const familyFilter = document.getElementById('familyFilter');
    const muscleFilter = document.getElementById('muscleFilter')
    const rows = document.querySelectorAll('tbody tr[data-family]');

    if (!familyFilter && !muscleFilter) return;

    function applyFilters(){
        const selectedFamily = familyFilter?.value || 'all';
        const selectedMuscle = muscleFilter?.value || 'all';

        rows.forEach(row=> {
            const rowFamily = row.getAttribute('data-family');
            const rowMuscle = row.getAttribute('data-muscle');

            const familyMatch = selectedFamily === 'all' || rowFamily === selectedFamily;
            const muscleMatch = selectedMuscle === 'all' || rowMuscle === selectedMuscle;
            row.style.display =(familyMatch && muscleMatch)?
                '' : 'none';
        });
    }

    if(familyFilter){
        familyFilter.addEventListener('change', applyFilters);
    }
    if(muscleFilter){
        muscleFilter.addEventListener('change', applyFilters)
    }
}

document.addEventListener('DOMContentLoaded', setUpFamilyFilter)
//à decom si ajax
//document.body.addEventListener('htmx:afterSwap', setUpFamilyFilter)