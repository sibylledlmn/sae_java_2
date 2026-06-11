document.addEventListener("DOMContentLoaded", function () {

    // TODO : fermer la modale en cliquant sur l'overlay


// GESTION DU CHANGEMENT D'ONGLETS'


    const tabs = document.querySelectorAll(".tab");
    const panels = document.querySelectorAll("[role='tabpanel']");

    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            tabs.forEach(t => t.classList.remove("active"));
            tab.classList.add("active");
            const panelId = tab.getAttribute("aria-controls");
            panels.forEach(p => p.classList.add("hidden"));
            document.getElementById(panelId).classList.remove("hidden");

        });
    });

    // MODALE CHANGEMENT MODE FACTURATION

    const btnMode = document.querySelectorAll(".btn-changer-mod");
    const modaleMode = document.getElementById("modal-mode");
    const texteModaleMode = document.getElementById("modal-mode-sub");
    const annulerMode = document.getElementById("btn-annuler-mode");
    const validerMode = document.getElementById("btn-confirmer-mode");


    btnMode.forEach(btn => {
        btn.addEventListener("click", () => {
            const modeActuel = btn.dataset.modeActuel;
            const modeCible = modeActuel === "REEL" ? "ECHEANCIER" : "REEL";
            const libelleActuel = modeActuel === "REEL" ? "Réel" : "Échéancier";
            const libelleCible = modeActuel === "REEL" ? "Échéancier" : "Réel";
            modaleMode.classList.remove("hidden");
            texteModaleMode.textContent = `Vous êtes actuellement en facturation au ${libelleActuel}. Confirmer le passage en mode ${libelleCible} ?`;
            validerMode.dataset.contratId = btn.dataset.contratId;
            validerMode.dataset.modeCible = modeCible;
        })
    })

    annulerMode.addEventListener("click", () => {
        modaleMode.classList.add("hidden")
    });




    // MODALE CHANGEMENT OFFRE TARIFAIRE

    const btnOffre = document.querySelectorAll(".btn-changer-offre");
    const modaleOffre = document.getElementById("modal-offre");
    const texteModaleOffre = document.getElementById("modal-offre-sub");
    const annulerOffre = document.getElementById("btn-annuler-offre");
    const validerOffre = document.getElementById("btn-confirmer-offre")

    btnOffre.forEach(btn => {
        btn.addEventListener("click", () => {
            const offreActuelle = btn.dataset.offreActuelle;
            const offreCible = offreActuelle === "CLASSIQUE" ? "HP/HC" : "Classique";
            const libelleActuelle = offreActuelle === "CLASSIQUE" ? "Classique" : "HP/HC";
            modaleOffre.classList.remove("hidden");
            texteModaleOffre.textContent = `Vous êtes actuellement sur l'offre ${libelleActuelle}. Confirmer le passage à l'offre ${offreCible} ?`;
            validerOffre.dataset.contratId = btn.dataset.contratId;
            validerOffre.dataset.offreCible = offreCible;
        })
    })

    annulerOffre.addEventListener("click", () =>{
        modaleOffre.classList.add("hidden");

    })


});
