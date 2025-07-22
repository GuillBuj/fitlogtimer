document.addEventListener('DOMContentLoaded', () => {
    // Init de Sortable.js
    const sortable = new Sortable(document.getElementById('sortable'), {
        animation: 150,
        //recupère l'ordre
        onEnd(evt){
            const order = sortable.toArray();
            const idsInputs = document.querySelectorAll("input[name='ids']");
            idsInputs.forEach((input, index) => {
                input.value = order[index];
            });
        }
});

    //synchro avant soumission du formulaire
    document.querySelector("form").addEventListener("submit", () => {
        const order = sortable.toArray();
        const idsInputs = document.querySelectorAll("input[name='ids']");
        idsInputs.forEach((input, i) => input.value = order[i]);
    });
});