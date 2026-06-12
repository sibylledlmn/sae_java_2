<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Mes contrats</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/contrats.css">
</head>
<body>

    <#include "sidebar-client.ftl">

    <main class="main">

        <div class="topbar">
            <div>
                <h1 class="page-title">Mes contrats</h1>
                <p class="page-sub">Gérez vos contrats ElectronVert</p>
            </div>
        </div>

        <div class="tabs" role="tablist">
            <div class="tab active" role="tab" aria-selected="true" aria-controls="panel-actifs" id="tab-actifs" onclick="showTab('actifs')">
                Actifs <span class="tab-count">${contratsActifs?size}</span>
            </div>
            <div class="tab" role="tab" aria-selected="false" aria-controls="panel-clos" id="tab-clos" onclick="showTab('clos')">
                Clôturés <span class="tab-count">${contratsClotures?size}</span>
            </div>
        </div>

        <!-- Panel : Actifs -->
        <div id="panel-actifs" class="contrats-grid" role="tabpanel" aria-labelledby="tab-actifs">

            <#list contratsActifs as contrat>
            <div class="contrat-card">
                <div class="cc-head">
                    <div class="cc-ref">Contrat ${contrat.id}</div>
                    <div class="cc-addr">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                        ${contrat.adressePostale}
                    </div>
                    <div class="badges">
                        <span class="badge green">Actif</span>
                        <#if contrat.getLibelleOffreTarifaire(contrat.offreTarifaire) == "Classique">
                        <span class="badge gray">Classique</span>
                            <#else>
                                <span class="badge blue">HP/HCs</span>
                        </#if>
                         <#if contrat.modeFacturation == "REEL">
                             <span class="badge blue">Réel</span>
                        <#else>
                            <span class="badge amber">Echéancier</span>
                         </#if>

                    </div>
                </div>
                <div class="cc-infos">
                    <div><div class="ci-label">Souscription</div><div class="ci-value">${contrat.dateSouscription?string("d MMMM yyyy")}</div></div>
                    <div><div class="ci-label">Offre tarifaire</div><div class="ci-value">${contrat.getLibelleOffreTarifaire(contrat.offreTarifaire)}</div></div>
                    <div><div class="ci-label">Mode facturation</div><div class="ci-value">${contrat.getLibelleModeFacturation(contrat.modeFacturation)}</div></div>
                    <div><div class="ci-label">Abonnement</div><div class="ci-value">
                            <#if contrat.getLibelleOffreTarifaire(contrat.offreTarifaire) == "Classique">
                                ${tarif.prixAbonnementClassique} € / mois
                            <#else>
                                ${tarif.prixAbonnementHPHC} € / mois
                            </#if>
                        </div></div>
                </div>
                <div class="cc-actions">
                    <button class="btn btn-secondary btn-changer-offre"
                            data-contrat-id="${contrat.id}"
                            data-offre-actuelle="${contrat.offreTarifaire}">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>
                        Changer d'offre
                    </button>
                    <button class="btn btn-secondary btn-changer-mod"
                            data-contrat-id="${contrat.id}"
                            data-mode-actuel="${contrat.modeFacturation}">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>
                        Changer de mode de facturation
                    </button>
                </div>
            </div>
            </#list>

        </div>

        <!-- Panel : Clôturés -->
        <div id="panel-clos" class="contrats-grid hidden" role="tabpanel" aria-labelledby="tab-clos">

            <#list contratsClotures as contrat>
            <div class="contrat-card clos">
                <div class="cc-head">
                    <div class="cc-ref">Contrat :${contrat.id}</div>
                    <div class="cc-addr">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                        ${contrat.adressePostale}
                    </div>
                    <div class="badges">
                        <span class="badge red">Clôturé</span>
                        <#if contrat.getLibelleOffreTarifaire(contrat.offreTarifaire) == "Classique">
                            <span class="badge gray">Classique</span>
                        <#else>
                            <span class="badge blue">HP/HCs</span>
                        </#if>
                        <#if contrat.modeFacturation == "REEL">
                            <span class="badge blue">Réel</span>
                        <#else>
                            <span class="badge amber">Echéancier</span>
                        </#if>

                    </div>
                </div>
                <div class="cc-infos clos">
                    <div><div class="ci-label">Souscription</div><div class="ci-value">${contrat.dateSouscription?string("d MMMM yyyy")}</div></div>
                    <div><div class="ci-label">Date de clôture</div><div class="ci-value">${contrat.dateFin?string("d MMMM yyyy")}</div></div>
                    <div><div class="ci-label">Offre tarifaire</div><div class="ci-value">${contrat.getLibelleOffreTarifaire(contrat.offreTarifaire)}</div></div>
                    <div><div class="ci-label">Mode facturation</div><div class="ci-value">${contrat.getLibelleModeFacturation(contrat.modeFacturation)}</div></div>
                </div>
                <div class="clos-info">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    Ce contrat est clôturé — aucune action possible.
                </div>
            </div>
            </#list>

        </div>

    </main>

    <!-- MODALE : Changement d'offre -->
    <div class="modal-overlay hidden" id="modal-offre" role="dialog" aria-modal="true" aria-labelledby="modal-offre-title">
        <div class="modal">
            <h2 class="modal-title" id="modal-offre-title">Changer d'offre tarifaire</h2>
            <p class="modal-sub" id="modal-offre-sub"></p>
            <div class="modal-footer">
                <button class="btn btn-ghost" id="btn-annuler-offre">Annuler</button>
                <button class="btn btn-primary" id="btn-confirmer-offre">Confirmer le changement</button>
            </div>
        </div>
    </div>

    <!-- MODALE : Changement de mode de facturation -->
    <div class="modal-overlay hidden" id="modal-mode" role="dialog" aria-modal="true" aria-labelledby="modal-mode-title">
        <div class="modal">
            <h2 class="modal-title" id="modal-mode-title">Changer de mode de facturation</h2>
            <p class="modal-sub" id="modal-mode-sub"></p>
            <div class="modal-footer">
                <button class="btn btn-ghost" id="btn-annuler-mode">Annuler</button>
                <button class="btn btn-primary" id="btn-confirmer-mode">Confirmer le changement</button>
            </div>
        </div>
    </div>

    <script src="/js/client/commun.js"></script>
    <script src="/js/client/contrats.js"></script>

</body>
</html>