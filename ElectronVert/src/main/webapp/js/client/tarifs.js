document.addEventListener("DOMContentLoaded", function () {

    const accordeon = document.getElementById("histo-toggle");
    const historique = document.getElementById("histo-body");
    const btnRechercher = document.getElementById("btn-rechercher");

    accordeon.addEventListener("click", () => {
        accordeon.classList.toggle("open");
        historique.classList.toggle("open");
    })

    btnRechercher.addEventListener("click", async () => {
        const date = document.getElementById("search-date").value;
        const dateFormatee = new Date(date).toLocaleDateString("fr-FR", { day: "numeric", month: "long", year: "numeric" });
        const response = await fetch(`/client/tarifs?date=${date}`);
        const data = await response.json();
        const resultEl = document.getElementById("search-result");
        if (data.found) {
            resultEl.innerHTML = `
                <strong>Tarif applicable au ${dateFormatee} :</strong>
                <div class="tarif-grid" style="margin-top: 12px;">
                    <div class="tarif-section">
                        <div class="tarif-section-title">Offre Classique</div>
                        <div class="tarif-row"><span class="tarif-row-label">Prix kWh</span><span class="tarif-row-value">${data.prixKwhClassique} €</span></div>
                        <div class="tarif-row"><span class="tarif-row-label">Abonnement mensuel</span><span class="tarif-row-value">${data.prixAbonnementClassique} €</span></div>
                    </div>
                    <div class="tarif-section">
                        <div class="tarif-section-title">Offre HP / HC</div>
                        <div class="tarif-row"><span class="tarif-row-label">Prix kWh Heures Pleines</span><span class="tarif-row-value">${data.prixKwhHP} €</span></div>
                        <div class="tarif-row"><span class="tarif-row-label">Prix kWh Heures Creuses</span><span class="tarif-row-value">${data.prixKwhHC} €</span></div>
                        <div class="tarif-row"><span class="tarif-row-label">Abonnement mensuel</span><span class="tarif-row-value">${data.prixAbonnementHPHC} €</span></div>
                    </div>
                </div>`;
        } else {
            resultEl.textContent = "Aucun tarif disponible pour cette date.";
        }
        resultEl.classList.add("visible");
    })

});
