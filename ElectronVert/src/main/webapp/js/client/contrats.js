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
            const descriptionCible = modeCible === "REEL"
                ? "La facturation au réel est basée sur votre consommation réelle mesurée chaque mois."
                : "La facturation par échéancier lisse vos paiements en mensualités fixes, régularisées en fin d'année.";
            modaleMode.classList.remove("hidden");
            texteModaleMode.textContent = `Vous êtes actuellement en facturation au ${libelleActuel}. Confirmer le passage en mode ${libelleCible} ? ${descriptionCible}`;
            validerMode.dataset.contratId = btn.dataset.contratId;
            validerMode.dataset.modeCible = modeCible;
        })
    })

    validerMode.addEventListener("click", async () => {
        const response = await fetch("/client/contrats", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `action=changerMode&contratId=${validerMode.dataset.contratId}&modeCible=${validerMode.dataset.modeCible}`
        });
        const data = await response.json();
        if (data.success) {
            window.location.reload();
        } else {
            texteModaleMode.textContent = data.message;
        }
    });

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
            // TODO : afficher un avertissement si des frais s'appliquent (hors mois anniversaire)
            // Piste : ajouter data-frais="true/false" sur le bouton depuis le template FreeMarker
            const offreActuelle = btn.dataset.offreActuelle;
            const offreCible = offreActuelle === "CLASSIQUE" ? "HPHC" : "CLASSIQUE";
            const libelleActuelle = offreActuelle === "CLASSIQUE" ? "Classique" : "HP/HC";
            const libelleCible = offreActuelle === "CLASSIQUE" ? "HP/HC" : "Classique";
            const descriptionOffreCible = offreCible === "CLASSIQUE"
                ? "L'offre Classique applique un tarif unique par kWh, quelle que soit l'heure de consommation."
                : "L'offre Heures Pleines / Heures Creuses propose deux tarifs selon l'heure : moins cher la nuit et le week-end.";
            modaleOffre.classList.remove("hidden");
            texteModaleOffre.textContent = `Vous êtes actuellement sur l'offre ${libelleActuelle}. Confirmer le passage à l'offre ${libelleCible} ? ${descriptionOffreCible}`;
            validerOffre.dataset.contratId = btn.dataset.contratId;
            validerOffre.dataset.offreCible = offreCible;
        })
    })

    validerOffre.addEventListener("click", async () => {
        const response = await fetch("/client/contrats", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `action=changerOffre&contratId=${validerOffre.dataset.contratId}&offreCible=${validerOffre.dataset.offreCible}`
        });
        const data = await response.json();
        if (data.success) {
            window.location.reload();
        } else {
            texteModaleOffre.textContent = data.message;
        }
    });

    annulerOffre.addEventListener("click", () =>{
        modaleOffre.classList.add("hidden");
    })


});
