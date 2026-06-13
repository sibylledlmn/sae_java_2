<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Tarifs en vigueur</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/tarifs.css">
</head>
<body>

<#include "sidebar-client.ftl">

<main class="main">

    <div class="topbar">
        <h1 class="page-title">Tarifs en vigueur</h1>
        <p class="page-sub">Consultez les tarifs ElectronVert applicables à vos contrats</p>
    </div>

    <!-- Tarif actuel -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                </svg>
                Tarif actuel
            </h2>
            <span class="badge-actuel">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="20 6 9 17 4 12"/>
                </svg>
                En vigueur
            </span>
        </div>
        <div class="tarif-grid">
            <div class="tarif-section">
                <div class="tarif-section-title">Offre Classique</div>
                <div class="tarif-row">
                    <span class="tarif-row-label">Prix kWh</span>
                    <span class="tarif-row-value">${tarif.prixKwhClassique} €</span>
                </div>
                <div class="tarif-row">
                    <span class="tarif-row-label">Abonnement mensuel</span>
                    <span class="tarif-row-value">${tarif.prixAbonnementClassique} €</span>
                </div>
            </div>
            <div class="tarif-section">
                <div class="tarif-section-title">Offre HP / HC</div>
                <div class="tarif-row">
                    <span class="tarif-row-label">Prix kWh Heures Pleines</span>
                    <span class="tarif-row-value">${tarif.prixKwhHP} €</span>
                </div>
                <div class="tarif-row">
                    <span class="tarif-row-label">Prix kWh Heures Creuses</span>
                    <span class="tarif-row-value">${tarif.prixKwhHC} €</span>
                </div>
                <div class="tarif-row">
                    <span class="tarif-row-label">Abonnement mensuel</span>
                    <span class="tarif-row-value">${tarif.prixAbonnementHPHC} €</span>
                </div>
            </div>
        </div>
        <div class="tarif-date">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            Applicable depuis le ${tarif.dateDebut}
        </div>
    </div>

    <!-- Recherche par date -->
    <div class="card">
        <div class="search-inner">
            <label class="search-label" for="search-date">Tarif applicable à une date :</label>
            <input class="form-input search-date-input" type="date" id="search-date">
            <button class="btn btn-primary" id="btn-rechercher">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
                Rechercher
            </button>
        </div>
        <div id="search-result" class="search-result" role="status"></div>
    </div>

    <!-- Accordéon historique -->
    <div class="histo-toggle" id="histo-toggle" role="button" aria-expanded="false">
        <span class="histo-toggle-label">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
            </svg>
            Historique des tarifs
        </span>
        <svg class="histo-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
             stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <polyline points="6 9 12 15 18 9"/>
        </svg>
    </div>
    <div class="histo-body" id="histo-body">
        <table class="htable">
            <thead>
                <tr>
                    <th>Date d'entrée en vigueur</th>
                    <th class="right">kWh Classique</th>
                    <th class="right">kWh HP</th>
                    <th class="right">kWh HC</th>
                    <th class="right">Abo. Classique</th>
                    <th class="right">Abo. HP/HC</th>
                </tr>
            </thead>
            <tbody>
            <#list tarifs as tarif>
                <tr>
                    <td><strong>${tarif.dateDebut}</strong> <#if tarif?index == 0><span class="badge-actuel-inline">● actuel</span></#if></td>
                    <td class="right">${tarif.prixKwhClassique} €</td>
                    <td class="right">${tarif.prixKwhHP} €</td>
                    <td class="right">${tarif.prixKwhHC} €</td>
                    <td class="right">${tarif.prixAbonnementClassique} €</td>
                    <td class="right">${tarif.prixAbonnementHPHC} €</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>

</main>

<script src="/js/client/commun.js"></script>
<script src="/js/client/tarifs.js"></script>

</body>
</html>
