<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Tableau de bord</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/dashboard.css">
</head>
<body>

    <aside class="sidebar" aria-label="Navigation principale">
        <div class="sidebar-logo">
            <div class="sidebar-logo-name">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#3ec97a" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
                ElectronVert
            </div>
            <div class="sidebar-logo-sub">Espace client</div>
        </div>
        <nav class="sidebar-nav">
            <div class="nav-section-label">Menu</div>
            <a href="#" class="nav-item active" aria-current="page">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                Tableau de bord
            </a>
            <a href="#" class="nav-item">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                Mes contrats
            </a>
            <a href="#" class="nav-item">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
                Mes factures
            </a>
            <a href="#" class="nav-item">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                Ma consommation
            </a>
            <div class="nav-section-label">Compte</div>
            <a href="#" class="nav-item">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                Mon profil
            </a>
            <a href="#" class="nav-item">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                Tarifs en vigueur
            </a>
            <a href="#" class="nav-item danger">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                Se déconnecter
            </a>
        </nav>
        <div class="sidebar-user">
            <div class="user-avatar" aria-hidden="true"> ${utilisateur.prenom[0]}${utilisateur.nom[0]}</div>
            <div>
                <div class="user-name">${utilisateur.prenom} ${utilisateur.nom}</div>
                <div class="user-role">Client</div>
            </div>
        </div>
    </aside>

    <main class="main">

        <div class="topbar">
            <div>
                <h1 class="page-title">Bonjour, ${utilisateur.prenom}</h1>
                <p class="page-sub">Voici un résumé de votre espace client</p>
            </div>
            <div class="date-badge">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                ${mois}
            </div>
        </div>

        <#if facturesImpayees?has_content>
        <div class="alert-bar warning" role="alert">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
            <span>Vous avez <strong>${facturesImpayees?size} factures impayé<#if facturesImpayees?size gt 1>es</#if></strong> — réglez avant le  ${prochaineEcheance} pour éviter des frais de relance.</span>
            <a href="#" class="alert-link">Payer maintenant →</a>
        </div>
        </#if>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon-wrap green" aria-hidden="true"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></div>
                <div class="stat-label">Contrats actifs</div>
                <div class="stat-value">${contratsActifs?size}</div>
                <div class="stat-sub">${contratsClotures?size} clôturé<#if contratsClotures?size gt 1>s</#if></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon-wrap red" aria-hidden="true"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg></div>
                <div class="stat-label">Factures impayées</div>
                <div class="stat-value">${facturesImpayees?size}</div>
                <div class="stat-sub">Total dû :  ${totalDu} €</div>
            </div>
            <#if derniereFacture??>
            <div class="stat-card">
                <div class="stat-icon-wrap amber" aria-hidden="true"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg></div>
                <div class="stat-label">Dernière facture</div>
                <div class="stat-value">${derniereFacture.montantTTC} €</div>
                <div class="stat-sub">Émise le ${derniereFacture.dateEmission}</div>
            </div>
            </#if>
<#--                marche pas si plusieurs contrats, à voir dans le futur si je fais un truc qui "tourne" et qui affiche -->
<#--            toutes les conso chacune leur tour-->

<#--            <#if consoMois??>-->
<#--            <div class="stat-card">-->
<#--                <div class="stat-icon-wrap teal" aria-hidden="true"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg></div>-->
<#--                <div class="stat-label">Conso. ce mois</div>-->
<#--                <div class="stat-value">{consoMois} kWh</div>-->
<#--                <div class="stat-sub">Contrat {contrat.id}}</div>-->
<#--            </div>-->
<#--            </#if>-->
        </div>

        <div class="content-grid">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
                        Factures récentes
                    </h2>
                    <a href="#" class="card-link">Tout voir →</a>
                </div>
                <table class="table">
                    <thead>
                        <tr>
                            <th>Référence</th>
                            <th>Échéance</th>
                            <th style="text-align:right;">Montant TTC</th>
                            <th style="text-align:right;">Statut</th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list facturesRecentes as facture>
                        <tr>
                            <td><div class="facture-ref">${facture.reference}</div><div class="facture-contrat">
                                Contrat ${facture.contratId} · ${facture.contratAdresse}</div></td>
                            <td><span class="facture-date">${facture.dateEcheance}</span></td>
                            <td class="facture-montant">${facture.montantTTC} €</td>
                            <td class="td-badge">
                                <#if facture.statut == "PAYEE">
                                    <span class="badge green">Payée</span>
                                <#elseif facture.statut == "IMPAYEE">
                                    <span class="badge red">Impayée</span>
                                <#else>
                                    <span class="badge amber">Émise</span>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                        Mes contrats
                    </h2>
                    <a href="#" class="card-link">Gérer →</a>
                </div>
                <#list contratsActifs as contrat>
                <div class="contrat-item">
                    <div>
                        <div class="contrat-ref">Contrat ${contrat.id}</div>
                        <div class="contrat-adresse">${contrat.adressePostale}</div>
                        <div class="contrat-badges">
                            <#if contrat.getLibelleOffreTarifaire(contrat.offreTarifaire) == "Classique">
                                <span class="badge gray">Classique</span>
                            <#else>
                                <span class="badge blue">HP/HC</span>
                            </#if>
                        </div>
                    </div>
                    <div class="contrat-meta">
                        <div class="contrat-meta-label">Mode de facturation</div>
                        <#if contrat.modeFacturation == "REEL">
                        <div class="contrat-meta-value">Réel</div>
                        <#else>
                            <div class="contrat-meta-value">Echéancier</div>
                        </#if>
                    </div>
                </div>
                </#list>
                <div class="card-footer">
                    <a href="#">+ Voir tous mes contrats</a>
                </div>
            </div>
        </div>

    </main>

</body>
</html>