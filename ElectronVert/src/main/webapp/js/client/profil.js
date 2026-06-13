document.addEventListener("DOMContentLoaded", function () {


    const infoLecture = document.getElementById("mode-lecture");
    const modeEdition = document.getElementById("mode-edition");
    const btnModifierInfo = document.getElementById("btn-modifier");
    const label = document.getElementById("btn-modifier-label");
    const accordeon = document.getElementById("mdp-toggle");
    const modifierMdp = document.getElementById("mdp-form");
    const btnEnregistrer = document.getElementById("btn-enregistrer");
    const erreurMdp = document.getElementById("erreur-mdp");
    const erreurInfo = document.getElementById("erreur-infos");
    const formInfos = document.getElementById("form-infos");
    const formMdp = document.getElementById("form-mdp");


    btnModifierInfo.addEventListener("click", () => {
        infoLecture.classList.toggle("hidden");
        modeEdition.classList.toggle("hidden");
        label.textContent = label.textContent === "Modifier" ? "Annuler" : "Modifier";
        btnEnregistrer.classList.toggle("hidden");
    })

    accordeon.addEventListener("click", () => {
        accordeon.classList.toggle("open");
        modifierMdp.classList.toggle("open");
        btnEnregistrer.classList.toggle("hidden");
    })

    btnEnregistrer.addEventListener("click", async () => {
        const accordeonOuvert = modifierMdp.classList.contains("open");
        let body;
        let erreurEl;

        if (accordeonOuvert) {
            if (!formMdp.reportValidity()) return;
            body = new URLSearchParams({
                mdpActuel: document.getElementById("mdp-actuel").value,
                mdpNouveau: document.getElementById("mdp-nouveau").value,
                mdpConfirm: document.getElementById("mdp-confirm").value
            });
            erreurEl = erreurMdp;
        } else {
            if (!formInfos.reportValidity()) return;
            body = new URLSearchParams({
                prenom: document.getElementById("prenom").value.trim(),
                nom: document.getElementById("nom").value.trim(),
                email: document.getElementById("email").value.trim()
            });
            erreurEl = erreurInfo;
        }

        const response = await fetch("/client/profil", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body.toString()
        });
        const data = await response.json();
        if (data.success) {
            window.location.reload();
        } else {
            erreurEl.textContent = data.message;
            erreurEl.classList.remove("hidden");
        }
    })



});
