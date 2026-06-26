<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Gestion des clients</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/clients-admin.css">
</head>
<body>

<#include "sidebar-admin.ftl">

<main class="main">

    <div class="topbar">
        <div>
            <h1 class="page-title">Gestion des clients</h1>
            <p class="page-sub"><#if email?has_content><#if nbClients == 0>Aucun résultat trouvé</#if><#else>${nbClients} clients enregistrés</#if></p>
        </div>
        <a href="/admin/clients/nouveau" class="btn btn-primary">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><line x1="20" y1="8" x2="20" y2="14"/><line x1="23" y1="11" x2="17" y2="11"/></svg>
            Nouveau client
        </a>
    </div>

    <div class="actions-bar">
        <form method="get" action="/admin/clients">
            <input class="search-input" type="text" name="email" placeholder="Rechercher par email..." id="recherche">
            <button type="submit">Rechercher</button><a href="/admin/clients" class="btn btn-secondary">Réinitialiser</a>

        </form>
    </div>

    <div class="card">
        <table class="ctable">
            <thead>
                <tr>
                    <th>Client</th>
                    <th>Contrats</th>
                    <th>Offres</th>
                    <th>Inscription</th>
                    <th style="text-align:right;">Actions</th>
                </tr>
            </thead>
            <tbody id="clients-tbody">
            <#if clients?size == 0>
                <tr><td colspan="5" style="text-align:center; color:#7a9a82; padding:24px;">Aucun client trouvé pour cette recherche.</td></tr>
            </#if>
            <#list clients as client>
                <tr  data-recherche="${client.email?lower_case}">
                    <td>
                        <div class="client-cell">
                            <div class="client-av" aria-hidden="true">ML</div>
                            <div>
                                <div class="client-name">${client.prenom} ${client.nom}</div>
                                <div class="client-email">${client.email}</div>
                            </div>
                        </div>
                    </td>
                    <td>${client.nbContratsActifs} actifs</td>
                    <td><#list client.offres as offre><#if offre == "Classique"><span class="badge gray">${offre}</span><#elseif offre == "HP/HC"><span class="badge blue">${offre}</span></#if></#list></td>
                    <td>${client.dateInscription}</td>
                    <td>
                        <div class="td-actions">
                            <a href="/admin/client?id=${client.id}" class="btn btn-secondary btn-sm">
                                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                                Détail
                            </a>
                        </div>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>

</main>

<script src="/js/admin/clients-admin.js"></script>

</body>
</html>
