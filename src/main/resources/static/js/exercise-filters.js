function setUpFamilyFilter(){
    const filter = document.getElementById('familyFilter');
    const rows = document.querySelectorAll('tbody tr[data-family]');

    if (!filter) return;

    filter.addEventListener('change', function(){
        const selectedFamily = this.value;

        rows.forEach(row=> {
            const  rowFamily = row.getAttribute('data-family');
            row.style.display =(selectedFamily === 'all' || rowFamily === selectedFamily ?
                '' : 'none')
        })
    })
}

document.addEventListener('DOMContentLoaded', setUpFamilyFilter)