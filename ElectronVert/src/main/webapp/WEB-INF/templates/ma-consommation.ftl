<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Ma consommation</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/consommation.css">
</head>
<body>

<#include "sidebar-client.ftl">

<#--TODO: que faire si bcp de relevé? faire page suivante -->

<main class="main">

    <div class="topbar">
        <div>
            <h1 class="page-title">Ma consommation</h1>
            <p class="page-sub">Historique de vos relevés de compteur</p>
        </div>
    </div>

    <!-- Filtre contrat -->
    <div class="filter-bar">
        <label class="filter-label" for="filtre-contrat">Contrat :</label>
        <select class="filter-select" id="filtre-contrat">
            <#list contrats as contrat>
                <option value="${contrat.id}">Contrat : ${contrat.id} - ${contrat.adressePostale}</option>
            </#list>
        </select>
    </div>

    <#list contrats as contrat>
<#--  premier contrat affiché par défault au chargement de la page-->
    <div id="info-${contrat.id}" class="contrat-info <#if contrat?index != 0>hidden</#if>">
        <div class="info-card"><div class="info-label">Offre</div><div class="info-value"> ${contrat.libelleOffre}</div></div>
        <div class="info-card"><div class="info-label">Mode facturation</div><div class="info-value"> ${contrat.libelleModeFacturation}</div></div>
        <div class="info-card"><div class="info-label">Dernier relevé</div><div class="info-value"> ${contrat.dernierReleve}</div></div>
    </div>
    </#list>



    <#list contrats as contrat>
    <div id="panel-${contrat.id}" class="panel-contrat <#if contrat?index != 0>hidden</#if>">
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/>
                        <line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/>
                        <line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/>
                    </svg>
                    Relevés — Contrat ${contrat.id}
                </h2>
            </div>
            <table class="rtable">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Type</th>
                        <#if contrat.hphc>
                            <th class="right">Index HP (kWh)</th>
                            <th class="right">Index HC (kWh)</th>
                            <th class="right">Conso. HP</th>
                            <th class="right">Conso. HC</th>
                        <#else>
                            <th class="right">Index total (kWh)</th>
                            <th class="right">Consommation</th>
                        </#if>
                        <th class="right">PDF</th>
                    </tr>
                </thead>
                <tbody>
                <#list contrat.releves as releve>
                    <tr>
                        <td>${releve.date}</td>
                        <td>
                            <span class="badge <#if releve.typeLibelle == "Ouverture">teal<#elseif releve.typeLibelle == "Clôture">gray<#else>blue</#if>">
                                ${releve.typeLibelle}
                            </span>
                        </td>
                        <#if contrat.hphc>
                            <td class="right">${releve.indexHP}</td>
                            <td class="right">${releve.indexHC}</td>
                            <td class="conso-hp">${releve.consoHP}</td>
                            <td class="conso-hc">${releve.consoHC}</td>
                        <#else>
                            <td class="right">${releve.indexTotal}</td>
                            <td class="conso-total">${releve.consoTotal}</td>
                        </#if>
                        <td class="right">
                            <#-- TODO: génération PDF à implémenter -->
                            <button class="btn btn-outline" disabled>
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                    <polyline points="7 10 12 15 17 10"/>
                                    <line x1="12" y1="15" x2="12" y2="3"/>
                                </svg>
                                PDF
                            </button>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
    </#list>


</main>

<script src="/js/client/commun.js"></script>
<script src="/js/client/consommation.js"></script>

</body>
</html>
