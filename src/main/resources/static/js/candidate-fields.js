const candidateList = document.querySelector('[data-candidate-list]');
const addCandidateButton = document.querySelector('[data-add-candidate]');
const candidateTemplate = document.querySelector('[data-candidate-template]');

if (candidateList && addCandidateButton && candidateTemplate) {
    const rows = () => candidateList.querySelectorAll('[data-candidate-row]');

    const updateNumbers = () => {
        rows().forEach((row, index) => {
            row.querySelector('[data-candidate-number]').textContent = index + 1;
        });
    };

    addCandidateButton.addEventListener('click', () => {
        const row = candidateTemplate.content.firstElementChild.cloneNode(true);

        candidateList.append(row);
        updateNumbers();
        row.querySelector('input').focus();
    });

    candidateList.addEventListener('click', ({target}) => {
        const removeButton = target.closest('[data-remove-candidate]');

        if (!removeButton) {
            return;
        }

        removeButton.closest('[data-candidate-row]').remove();
        updateNumbers();
    });
}
