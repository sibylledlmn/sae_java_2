<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Mes factures</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/factures.css">
</head>
<body>

<#include "sidebar-client.ftl">

<main class="main">

    <div class="topbar">
        <div>
            <h1 class="page-title">Mes factures</h1>
            <p class="page-sub">Consultez et payez vos factures</p>
        </div>
    </div>

    <!-- Barre onglets + filtre -->
    <div class="tabs-bar">
        <div class="tabs" role="tablist">
            <div class="tab active" role="tab" aria-selected="true" id="tab-toutes" aria-controls="panel-toutes">
                Toutes <span class="tab-count" id="count-toutes">${factures?size}</span>
            </div>
            <div class="tab" role="tab" aria-selected="false" id="tab-apayer" aria-controls="panel-apayer">
                À payer <span class="tab-count red" id="count-apayer">${facturesAPayer?size}</span>
            </div>
        </div>
        <div class="filter-wrap">
            <label class="filter-label" for="filtre-contrat">Contrat :</label>
            <select class="filter-select" id="filtre-contrat">
                <option value="tous">Tous les contrats</option>
            <#list contrats as c>
                <option value="${c.id}">${c.id} - ${c.adressePostale}</option>
            </#list>
            </select>
        </div>
    </div>

    <!-- Panel : Toutes -->
    <div id="panel-toutes" role="tabpanel" aria-labelledby="tab-toutes">
        <div class="card">
            <table class="table">
                <thead>
                <tr>
                    <th>Référence</th>
                    <th>Contrat</th>
                    <th>Émission</th>
                    <th>Échéance</th>
                    <th class="right">Montant TTC</th>
                    <th class="right">Statut</th>
                    <th class="right">Actions</th>
                </tr>
                </thead>
                <tbody id="tbody-toutes">
                <#list factures as facture>
                    <tr  data-contrat="${facture.contratId}">
                        <td><div class="f-ref">${facture.reference}</div></td>
                        <td><span class="f-ref">CT-${facture.contratId}</span><br><span class="f-contrat">${facture.contratAdresse}</span></td>
                        <td>${facture.dateEmission}</td>
                        <td>${facture.dateEcheance}</td>
                        <td class="right">${facture.montantTTC}</td>
                        <td class="right">
                            <#if facture.statut == "PAYEE">
                                <span class="badge green">Payée</span>
                            <#elseif facture.statut == "IMPAYEE">
                                <span class="badge red">Impayée</span>
                            <#else>
                                <span class="badge gray">Émise</span>
                            </#if>
                        </td>
                        <td class="right">
                            <div class="f-actions">
                                <#if facture.statut != "PAYEE">
                                    <button class="btn btn-primary btn-payer"
                                            data-facture-id="${facture.id}"
                                            data-reference="${facture.reference}"
                                            data-montant="${facture.montantTTC}">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                             stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                             stroke-linejoin="round" aria-hidden="true">
                                            <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                                            <line x1="1" y1="10" x2="23" y2="10"/>
                                        </svg>
                                        Payer
                                    </button>
                                </#if>
                                <button class="btn btn-outline">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                         stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                         aria-hidden="true">
                                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                        <polyline points="7 10 12 15 17 10"/>
                                        <line x1="12" y1="15" x2="12" y2="3"/>
                                    </svg>
                                    PDF
                                </button>
                            </div>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
            <div id="empty-toutes" class="empty hidden">Aucune facture pour ce contrat.</div>
        </div>
    </div>

    <!-- Panel : À payer -->
    <div id="panel-apayer" class="hidden" role="tabpanel" aria-labelledby="tab-apayer">
        <div class="payer-summary">
            <div class="sum-card">
                <div class="sum-label">Factures en attente de paiement</div>
                <div class="sum-value" id="sum-nb">${facturesAPayer?size}</div>
            </div>
            <div class="sum-card">
                <div class="sum-label">Total à régler</div>
                <div class="sum-value" id="sum-total"> ${totalAPayer}</div>
            </div>
        </div>
        <div class="alert-bar warning" role="alert">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                <line x1="12" y1="9" x2="12" y2="13"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
            <span>Réglez vos factures avant leur date d'échéance pour éviter des frais de relance.</span>
        </div>
        <div class="card">
            <table class="table">
                <thead>
                <tr>
                    <th>Référence</th>
                    <th>Contrat</th>
                    <th>Émission</th>
                    <th>Échéance</th>
                    <th class="right">Montant TTC</th>
                    <th class="right">Frais de relance</th>
                    <th class="right">Total à payer</th>
                    <th class="right">Action</th>
                </tr>
                </thead>
                <tbody id="tbody-apayer">
                <#list facturesAPayer as facture>
                    <tr  data-contrat="${facture.contratId}">
                        <td><div class="f-ref">${facture.reference}</div></td>
                        <td><span class="f-ref">CT-${facture.contratId}</span><br><span class="f-contrat">${facture.contratAdresse}</span></td>
                        <td>${facture.dateEmission}</td>
                        <td>${facture.dateEcheance}</td>
                        <td class="right">${facture.montantTTC}</td>
                        <td class="right"><#if (facture.montantFraisRelanceBrut > 0)>${facture.montantFraisRelance}<#else>—</#if></td>
                        <td class="right">${facture.montantTotalFraisInclus}</td>
                        <td class="right">
                            <div class="f-actions">

                                    <button class="btn btn-primary btn-payer"
                                            data-facture-id="${facture.id}"
                                            data-reference="${facture.reference}"
                                            data-montant="${facture.montantTotalFraisInclus}">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                             stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                             stroke-linejoin="round" aria-hidden="true">
                                            <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                                            <line x1="1" y1="10" x2="23" y2="10"/>
                                        </svg>
                                        Payer
                                    </button>

                                <button class="btn btn-outline">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                         stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                         aria-hidden="true">
                                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                        <polyline points="7 10 12 15 17 10"/>
                                        <line x1="12" y1="15" x2="12" y2="3"/>
                                    </svg>
                                    PDF
                                </button>
                            </div>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
            <div id="empty-apayer" class="empty hidden">Aucune facture en attente de paiement pour ce contrat.</div>
        </div>
    </div>

</main>

<!-- MODALE : Confirmation de paiement -->
<div class="modal-overlay hidden" id="modal-paiement" role="dialog" aria-modal="true"
     aria-labelledby="modal-paiement-title">
    <div class="modal">
        <h2 class="modal-title" id="modal-paiement-title">Confirmer le paiement</h2>
        <p class="modal-body">Facture <strong id="paiement-ref"></strong></p>
        <div class="modal-amount" id="paiement-montant"></div>
        <p id="paiement-erreur" class="hidden" style="color: #c0392b; font-size: 13px;">Le paiement a échoué, veuillez réessayer.</p>
        <div class="modal-footer">
            <button class="btn btn-ghost" id="btn-annuler-paiement">Annuler</button>
            <button class="btn btn-primary" id="btn-confirmer-paiement">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="20 6 9 17 4 12"/>
                </svg>
                Valider
            </button>
        </div>
    </div>
</div>

<script src="/js/client/commun.js"></script>
<script src="/js/client/factures.js"></script>

</body>
</html>
