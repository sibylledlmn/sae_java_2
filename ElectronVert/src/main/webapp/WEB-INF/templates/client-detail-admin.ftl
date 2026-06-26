<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Détail client</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/client-detail-admin.css">
</head>
<body>

<#include "sidebar-admin.ftl">

<main class="main">

    <div class="topbar">
        <div class="breadcrumb">
            <a href="/admin/clients">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="15 18 9 12 15 6"/></svg>
                Clients
            </a>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">${client.prenom} ${client.nom}</span>
        </div>
    </div>

    <!-- En-tête client -->
    <div class="client-header">
        <div class="client-av-big" aria-hidden="true">${client.prenom?substring(0,1)}${client.nom?substring(0,1)} </div>
        <div class="client-identity">
            <!-- Mode lecture -->
            <div id="mode-lecture">
                <div class="client-fullname">${client.prenom} ${client.nom}</div>
                <div class="client-email">${client.email}</div>
                <div class="client-since">Client depuis le ${client.dateInscription}</div>
            </div>
            <!-- Mode édition -->
            <form method="post" action="/admin/client?id=${client.id}">
            <div id="mode-edition" style="display:none;">
                <div class="edit-name-grid">
                    <div>
                        <label class="form-label">Prénom</label>
                        <input class="form-input" type="text" name="prenom" value="${client.prenom}">
                    </div>
                    <div>
                        <label class="form-label">Nom</label>
                        <input class="form-input" type="text" name="nom" value="${client.nom}">
                    </div>
                </div>
                <div>
                    <label class="form-label">Adresse e-mail</label>
                    <input class="form-input" type="email" name="email" value="${client.email}">
                </div>
                <div class="edit-actions">
                    <button class="btn btn-primary btn-sm">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg>
                        Enregistrer
                    </button>
                    <button class="btn btn-secondary btn-sm" id="btn-annuler">Annuler</button>
                </div>
            </div>
            </form>
        </div>
        <div class="client-header-actions">
            <button class="btn btn-secondary btn-sm" id="btn-modifier">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                Modifier
            </button>
            <div class="client-stat">
                <div class="client-stat-label">Contrats actifs</div>
                <div class="client-stat-value">${client.nbContratsActifs}</div>
            </div>
        </div>
    </div>

    <!-- Grille contrats + factures -->
    <div class="detail-grid">

        <!-- Contrats -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                    Contrats
                </h2>
                <a href="#" class="btn btn-primary btn-sm">
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    Ajouter
                </a>
            </div>
            <#list contratsActifs as contrat>
            <div class="contrat-item">
                <div>
                    <div class="contrat-ref">Contrat n°${contrat.id}</div>
                    <div class="contrat-addr">${contrat.adressePostale}</div>
                    <div class="contrat-badges">
                        <#if contrat.statut?string == "ACTIF">
                            <span class="badge green">Actif</span>
                        <#else>
                            <span class="badge gray">Clôturé</span>
                        </#if>

                        <#if contrat.libelleOffre == "Classique">
                            <span class="badge gray">Classique</span>
                        <#else>
                            <span class="badge blue">HP/HC</span>
                        </#if>

                        <#if contrat.libelleMode == "Réel">
                            <span class="badge blue">Réel</span>
                        <#else>
                            <span class="badge amber">Échéancier</span>
                        </#if>
                    </div>
                </div>
                <a href="#" class="btn btn-secondary btn-sm">
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    Voir
                </a>
            </div>
        </#list>

            <div class="contrats-footer">
                <a href="#" class="contrats-footer-link">Voir tous les contrats (dont clôturés) →</a>
            </div>
        </div>

        <!-- Factures -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
                    Factures récentes
                </h2>
                <a href="#" class="btn btn-secondary btn-sm">Tout voir</a>
            </div>
            <table class="mtable">
                <thead>
                    <tr>
                        <th>Référence</th>
                        <th>Échéance</th>
                        <th class="right">Montant</th>
                        <th class="right">Statut</th>
                    </tr>
                </thead>
                <tbody>
                <#list facturesRecentes as facture>
                    <tr>
                        <td>${facture.reference}</td>
                        <td class="muted">${facture.dateEcheance}</td>
                        <td class="right">${facture.montantTTC}</td>
                        <td class="right">
                            <#if facture.statut?string == "PAYEE">
                                <span class="badge green">Payée</span>
                            <#elseif facture.statut?string == "IMPAYEE">
                                <span class="badge red">Impayée</span>
                            <#else>
                                <span class="badge amber">Émise</span>
                            </#if>
                        </td>
                    </tr>
                </#list>.
                </tbody>
            </table>
        </div>

    </div>

    <!-- Relevés de consommation -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                Derniers relevés de consommation
            </h2>
            <a href="#" class="btn btn-secondary btn-sm">Tout voir</a>
        </div>
        <table class="mtable">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Contrat</th>
                    <th>Type</th>
                    <th class="right">Index total</th>
                    <th class="right">Index HP</th>
                    <th class="right">Index HC</th>
                    <th class="right">Consommation</th>
                </tr>
            </thead>
            <tbody>
                <#list relevesRecents as releve>
                <tr>
                    <td>${releve.date}</td>
                    <td class="muted">${releve.contratRef}</td>
                    <td><span class="badge blue">${releve.typeLibelle}</span></td>
                    <td class="right<#if !releve.indexTotal??> muted</#if>">${releve.indexTotal!"—"}</td>
                    <td class="right<#if !releve.indexHP??> muted</#if>">${releve.indexHP!"—"}</td>
                    <td class="right<#if !releve.indexHC??> muted</#if>">${releve.indexHC!"—"}</td>
                    <td class="conso">
                        <#if releve.consoTotal??>
                            ${releve.consoTotal}
                        <#elseif releve.consoHP??>
                            HP: ${releve.consoHP} / HC: ${releve.consoHC}
                        <#else>
                            —
                        </#if>
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>
    </div>

</main>

<script src="/js/admin/client-detail-admin.js"></script>

</body>
</html>
