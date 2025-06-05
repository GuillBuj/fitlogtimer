function applyColor(element) {
    const color = element.getAttribute('data-color') || '#333333';
    const row = element.closest('tr');

    const colorInput = row.querySelector('input[type="color"]');
    const preview = row.querySelector('.color-preview');

    colorInput.value = color;
    preview.style.backgroundColor = color;

    const popup = element.closest('.color-selector-container').querySelector('.color-popup-modal');
    if (popup) popup.style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.show-more-colors-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            const popup = this.closest('.color-selector-container').querySelector('.color-popup-modal');
            popup.style.display = popup.style.display === 'none' ? 'grid' : 'none';
            e.stopPropagation();
        });
    });

    // Ferme le popup quand on clique ailleurs
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.color-selector-container')) {
            document.querySelectorAll('.color-popup-modal').forEach(p => {
                p.style.display = 'none';
            });
        }
    });
});

