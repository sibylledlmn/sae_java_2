<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Administration</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/dashboard-admin.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js"></script>
</head>
<body>

<#include "sidebar-admin.ftl">

<main class="main">

    <div class="topbar">
        <div>
            <h1 class="page-title">Tableau de bord</h1>
            <p class="page-sub">Vue d'ensemble — ElectronVert</p>
        </div>
        <div class="date-badge">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            ${mois}
        </div>
    </div>

    <!-- Statistiques -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon-wrap green" aria-hidden="true">
                <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            </div>
            <div class="stat-label">Clients actifs</div>
            <div class="stat-value">${nbClientsActif}</div>
            <div class="stat-sub">+${nouveauxClientsMois} ce mois</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon-wrap blue" aria-hidden="true">
                <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            </div>
            <div class="stat-label">Contrats actifs</div>
            <div class="stat-value">${nbContratsActifs}</div>
            <div class="stat-sub"> ${nbContratsClotures} clôturés</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon-wrap amber" aria-hidden="true">
                <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
            </div>
            <div class="stat-label">CA mensuel</div>
            <div class="stat-value">${caMensuel}</div>
            <div class="stat-sub">${variationCA}</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon-wrap red" aria-hidden="true">
                <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
            </div>
            <div class="stat-label">Impayées</div>
            <div class="stat-value">${nbImpayee}</div>
            <div class="stat-sub">Total : ${totalImpayee}</div>
        </div>
    </div>

    <div class="grid-2" style="margin-bottom: 16px;">

        <!-- Impayées avec relances -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    Impayées avec relances en cours
                </h2>
                <a href="/admin/factures" class="card-link">Voir toutes →</a>
            </div>
            <table class="impayees-table">
                <thead>
                    <tr>
                        <th>Client / Facture</th>
                        <th>Montant TTC</th>
                        <th>Relance</th>
                    </tr>
                </thead>
                <tbody>
                <#list  facturesImpayees as facture>
                    <tr>
                        <td><div class="client-name">${facture.nomClient}</div><div class="facture-info">${facture.reference} · depuis le ${facture.dateEcheance}</div></td>
                        <td class="td-right"><span class="montant">${facture.montantTTC}</span></td>
                        <td class="td-right"><span class="badge ${facture.badgeClass}">${facture.nbRelances}</span></td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>

        <!-- Répartition des offres -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
                    </svg>
                    Répartition des offres
                </h2>
            </div>
            <div class="donut-wrap">
                <div class="canvas-wrap">
                    <canvas id="donutChart"
                            data-classique="${pctClassique}"
                            data-hphc="${pctHphc}"
                            role="img"
                            aria-label="Répartition des offres : ${pctClassique}% Classique, ${pctHphc}% HP/HC">
                        Classique ${pctClassique}%, HP/HC ${pctHphc}%
                    </canvas>
                </div>
                <div class="legend">
                    <div class="leg-item"><span class="leg-dot classique"></span>Classique — ${pctClassique}%</div>
                    <div class="leg-item"><span class="leg-dot hphc"></span>HP/HC — ${pctHphc}%</div>
                </div>
            </div>
            <div class="donut-wrap" style="margin-top: 16px;">
                <div class="canvas-wrap">
                    <canvas id="donutChartMode"
                            data-reel="${pctReel}"
                            data-echeancier="${pctEcheancier}"
                            role="img"
                            aria-label="Répartition des modes : ${pctReel}% Réel, ${pctEcheancier}% Échéancier">
                        Réel ${pctReel}%, Échéancier ${pctEcheancier}%
                    </canvas>
                </div>
                <div class="legend">
                    <div class="legend-section-label">Mode facturation</div>
                    <div class="leg-item"><span class="leg-dot reel"></span>Réel — ${pctReel}%</div>
                    <div class="leg-item"><span class="leg-dot echeancier"></span>Échéancier — ${pctEcheancier}%</div>
                </div>
            </div>
        </div>

    </div>

    <!-- Derniers clients -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><line x1="20" y1="8" x2="20" y2="14"/><line x1="23" y1="11" x2="17" y2="11"/>
                </svg>
                Derniers clients créés
            </h2>
            <a href="/admin/clients" class="card-link">Voir tous les clients →</a>
        </div>
        <div class="clients-grid">
            <#list derniersClients as c>
            <div class="client-card">
                <div class="client-avatar" aria-hidden="true">${c.prenom?substring(0,1)}${c.nom?substring(0,1)}</div>
                <div>
                    <div class="client-name-main">${c.prenom} ${c.nom}</div>
                    <div class="client-date">Inscrit le 08/06/2026</div>
                    <div class="client-badges"><span class="badge green">Actif</span><span class="badge gray">Classique</span></div>
                </div>
            </div>
            </#list>
        </div>
    </div>

</main>

<script src="/js/admin/dashboard-admin.js"></script>

</body>
</html>
