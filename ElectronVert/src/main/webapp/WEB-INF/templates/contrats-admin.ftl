<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Gestion des contrats</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/contrats-admin.css">
</head>
<body>

<#include "sidebar-admin.ftl">

<main class="main">

    <div class="topbar">
        <div>
            <h1 class="page-title">Gestion des contrats</h1>
        </div>
    </div>

    <!-- Filtres -->
    <form method="get" action="/admin/contrats" id="form-filtres">
        <div class="filters-bar">
            <input class="search-input" type="text" name="search" value="${search}" placeholder="Rechercher par n° de contrat ou email..." id="search">
            <select class="filter-select" name="statut" id="f-statut">
                <option value="" <#if statut == "">selected</#if>>Tous les statuts</option>
                <option value="ACTIF" <#if statut == "ACTIF">selected</#if>>Actif</option>
                <option value="CLOTURE" <#if statut == "CLOTURE">selected</#if>>Clôturé</option>
            </select>
            <select class="filter-select" name="offre" id="f-offre">
                <option value="" <#if offre == "">selected</#if>>Toutes les offres</option>
                <option value="CLASSIQUE" <#if offre == "CLASSIQUE">selected</#if>>Classique</option>
                <option value="HPHC" <#if offre == "HPHC">selected</#if>>HP/HC</option>
            </select>
            <select class="filter-select" name="mode" id="f-mode">
                <option value="" <#if mode == "">selected</#if>>Tous les modes de facturation</option>
                <option value="REEL" <#if mode == "REEL">selected</#if>>Réel</option>
                <option value="ECHEANCIER" <#if mode == "ECHEANCIER">selected</#if>>Échéancier</option>
            </select>
        </div>
    </form>

    <div class="card">
        <table class="ctable">
            <thead>
                <tr>
                    <th>Contrat</th>
                    <th>Client</th>
                    <th>Offre</th>
                    <th>Mode</th>
                    <th>Souscription</th>
                    <th>Statut</th>
                    <th class="right">Actions</th>
                </tr>
            </thead>
            <tbody id="contrats-tbody">
            <#list contrats as c>
                <tr >
                    <td><div class="ct-ref">Contrat n°${c.id}</div><div class="ct-addr">${c.adresse}</div></td>
                    <td><span class="ct-client">${c.identiteClient}</span></td>
                    <#if c.libelleOffre == "Classique">
                    <td><span class="badge gray">Classique</span></td>
                    <#else>
                        <td><span class="badge blue">HP/HC</span></td>
                    </#if>
                    <#if c.libelleMode == "Réel">
                    <td><span class="badge blue">Réel</span></td>
                        <#else>
                            <td><span class="badge amber">Échéancier</span></td>
                    </#if>
                    <td>${c.dateSouscription}</td>
                    <#if c.statut == "ACTIF">
                    <td><span class="badge green">Actif</span></td>
                    <#else>
                    <td><span class="badge red">Clôturé</span></td>
                    </#if>

                    <td><div class="td-actions"><a href="/admin/contrat?id=${c.id}" class="btn btn-secondary btn-sm"><svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>Voir</a></div></td>
                </tr>
            </#list>
            </tbody>
        </table>
        <div class="pagination">
<#--TODO pour l'instant on laisse le code en dur de la faussse paginition, absolument à mettre en plcae pour toutes les listes de tous les template-->
            <span>Affichage de 1 à 7 sur ${contrats?size} résultats</span>
            <div class="pages">
                <div class="page-btn active">1</div>
                <div class="page-btn">2</div>
                <div class="page-btn">3</div>
                <div class="page-btn">…</div>
                <div class="page-btn">21</div>
            </div>
        </div>
    </div>

</main>

<script src="/js/admin/contrats-admin.js"></script>

</body>
</html>
