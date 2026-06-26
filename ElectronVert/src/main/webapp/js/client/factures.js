document.addEventListener("DOMContentLoaded", function () {


    // GESTION DU CHANGEMENT D'ONGLETS'
  initTabs();

    // FILTRE CONTRAT — soumission automatique du formulaire au changement
    document.getElementById("filtre-contrat").addEventListener("change", function () {
        document.getElementById("form-filtre").submit();
    });

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
