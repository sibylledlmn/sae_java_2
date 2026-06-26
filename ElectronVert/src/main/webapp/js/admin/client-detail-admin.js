document.addEventListener("DOMContentLoaded", function () {

    const btnModifier = document.getElementById("btn-modifier");
    const btnAnnuler  = document.getElementById("btn-annuler");
    const modeLecture = document.getElementById("mode-lecture");
    const modeEdition = document.getElementById("mode-edition");

    btnModifier.addEventListener("click", function () {
        modeLecture.style.display = "none";
        modeEdition.style.display = "block";
        btnModifier.style.display = "none";
    });

    btnAnnuler.addEventListener("click", function () {
        modeEdition.style.display = "none";
        modeLecture.style.display = "block";
        btnModifier.style.display = "";
    });

});
