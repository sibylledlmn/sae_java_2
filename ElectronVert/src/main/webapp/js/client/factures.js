document.addEventListener("DOMContentLoaded", function () {


    // GESTION DU CHANGEMENT D'ONGLETS'
  initTabs();

  // FILTRE
    const tbodyToutes = document.getElementById("tbody-toutes");
    const tbodyApayer = document.getElementById("tbody-apayer");
    const filtre = document.getElementById("filtre-contrat");
    filtre.addEventListener("change", () => {
        const valeur = filtre.value;
        let filtreEstSurTous;
        if (valeur === "tous") {
            filtreEstSurTous = true;
        } else {
            filtreEstSurTous = false;
        }

        // Filtrer le panel toutes les factures
        let visibleToutes = 0;
        const lignesToutes = tbodyToutes.querySelectorAll("tr");

        lignesToutes.forEach(row => {
            const contratDeLaLigne = row.dataset.contrat;
            let ligneCorrespondAuFiltre;
            if (contratDeLaLigne === valeur) {
                ligneCorrespondAuFiltre = true;
            } else {
                ligneCorrespondAuFiltre = false;
            }
            if (filtreEstSurTous || ligneCorrespondAuFiltre) {
                row.style.display = "";
                visibleToutes++;
            } else {
                row.style.display = "none";
            }
        });

        document.getElementById("empty-toutes").classList.toggle("hidden", visibleToutes > 0);
        document.getElementById("count-toutes").textContent = visibleToutes;

        // Filtrer le panel "À payer"
        let visibleApayer = 0;
        const lignesApayer = tbodyApayer.querySelectorAll("tr");

        lignesApayer.forEach(row => {
            const contratDeLaLigne = row.dataset.contrat;

            let ligneCorrespondAuFiltre;
            if (contratDeLaLigne === valeur) {
                ligneCorrespondAuFiltre = true;
            } else {
                ligneCorrespondAuFiltre = false;
            }

            if (filtreEstSurTous || ligneCorrespondAuFiltre) {
                row.style.display = "";
                visibleApayer++;
            } else {
                row.style.display = "none";
            }
        });

        document.getElementById("empty-apayer").classList.toggle("hidden", visibleApayer > 0);
        document.getElementById("count-apayer").textContent = visibleApayer;

    })

    // MODALE DE PAIEMENT

    //ouvrir la modale
    const btnPayer = document.querySelectorAll(".btn-payer");
    const modale = document.getElementById("modal-paiement");
    let factureIdEnCours = null;

    btnPayer.forEach(btn => {
        btn.addEventListener("click", () => {
            factureIdEnCours = btn.dataset.factureId;
            const montant = btn.dataset.montant;
            const reference = btn.dataset.reference;
            modale.classList.remove("hidden");
            document.getElementById("paiement-montant").textContent = montant ;
            document.getElementById("paiement-ref").textContent = reference
        })
    })

    //annuler
    const btnAnnulerPaiement = document.getElementById("btn-annuler-paiement");
    btnAnnulerPaiement.addEventListener("click", () => {
        modale.classList.add("hidden");

    })

    // confirmer le paiement
    const btnConfirmerPaiement = document.getElementById("btn-confirmer-paiement");
    btnConfirmerPaiement.addEventListener("click", async () => {
        const response = await fetch("/client/factures", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `factureId=${factureIdEnCours}`
        });
        const data = await response.json();
        if (data.success) {
            window.location.reload();
        } else {
            document.getElementById("paiement-erreur").classList.remove("hidden")
        }
    })


});
