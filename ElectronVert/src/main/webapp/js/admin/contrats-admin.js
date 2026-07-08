document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("form-filtres");

    document.getElementById("f-statut").addEventListener("change", () => form.submit());
    document.getElementById("f-offre").addEventListener("change",  () => form.submit());
    document.getElementById("f-mode").addEventListener("change",   () => form.submit());

    // La recherche texte se soumet à l'appui sur Entrée (comportement natif du form)
});

