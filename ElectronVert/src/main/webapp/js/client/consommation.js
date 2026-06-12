document.addEventListener("DOMContentLoaded", function () {

    const filtre = document.getElementById("filtre-contrat");
    const contratInfo = document.querySelectorAll(".contrat-info");
    const panelContrat = document.querySelectorAll(".panel-contrat");

    filtre.addEventListener("change", () => {
        contratInfo.forEach(contrat => {
            contrat.classList.add("hidden");
        })
        panelContrat.forEach(panel => {
            panel.classList.add("hidden")
        })
        const valeur = filtre.value;
        document.getElementById(`info-${valeur}`).classList.remove("hidden");
        document.getElementById(`panel-${valeur}`).classList.remove("hidden");;

    })

});
