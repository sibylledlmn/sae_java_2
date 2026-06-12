// GESTION DES ONGLETS
// Utilisation : initTabs() — les .tab doivent avoir un attribut aria-controls pointant vers l'id du panel cible

function initTabs(onTabChange) {
    const tabs = document.querySelectorAll(".tab");
    const panels = document.querySelectorAll("[role='tabpanel']");

    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            tabs.forEach(t => {
                t.classList.remove("active");
                t.setAttribute("aria-selected", "false");
            });
            tab.classList.add("active");
            tab.setAttribute("aria-selected", "true");
            const panelId = tab.getAttribute("aria-controls");
            panels.forEach(p => p.classList.add("hidden"));
            document.getElementById(panelId).classList.remove("hidden");
            if (onTabChange) onTabChange(panelId);
        });
    });
}
