document.addEventListener("DOMContentLoaded", () => {

    const table = document.querySelector("table");
    const tbody = table.querySelector("tbody");

    document.querySelectorAll(".sortable-year").forEach(header => {

        header.addEventListener("click", () => {
            const year = header.dataset.year;

            const rows = Array.from(tbody.querySelectorAll("tr"));

            rows.sort((rowA, rowB) => {
                const cellA = rowA.querySelector(`td[data-year='${year}']`);
                const cellB = rowB.querySelector(`td[data-year='${year}']`);

                const setsA = cellA ? parseInt(cellA.dataset.sets || "0", 10) : 0;
                const setsB = cellB ? parseInt(cellB.dataset.sets || "0", 10) : 0;

                return setsB - setsA;
            });

            rows.forEach(row => tbody.appendChild(row));
        });
    });
});
