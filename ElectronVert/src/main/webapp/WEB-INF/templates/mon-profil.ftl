<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ElectronVert – Mon profil</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/commun.css">
    <link rel="stylesheet" href="/css/profil.css">
</head>
<body>

<#include "sidebar-client.ftl">

<main class="main">

    <div class="topbar">
        <h1 class="page-title">Mon profil</h1>
        <p class="page-sub">Gérez vos informations personnelles</p>
    </div>

    <div class="form-wrap">

        <!-- Informations personnelles -->
        <div class="card">
            <div class="avatar-block">
                <div class="avatar-big" aria-hidden="true">  ${utilisateur.prenom[0]}${utilisateur.nom[0]}</div>
                <div>
                    <div class="avatar-name">${utilisateur.prenom} ${utilisateur.nom}</div>
                    <div class="avatar-sub">Client depuis le ${datePremierContrat}</div>
                </div>
            </div>

            <div class="card-header">
                <h2 class="card-title">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                    </svg>
                    Informations personnelles
                </h2>
                <button class="btn btn-secondary" id="btn-modifier">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                    <span id="btn-modifier-label">Modifier</span>
                </button>
            </div>

            <!-- Mode lecture -->
            <div id="mode-lecture" class="info-grid">
                <div class="info-item">
                    <div class="info-label">Prénom</div>
                    <div class="info-value">${utilisateur.prenom}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Nom</div>
                    <div class="info-value">${utilisateur.nom}</div>
                </div>
                <div class="info-item full">
                    <div class="info-label">Adresse e-mail</div>
                    <div class="info-value">${utilisateur.email}</div>
                </div>
            </div>

            <!-- Mode édition -->
            <form id="form-infos">
            <div id="mode-edition" class="hidden">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="prenom">Prénom</label>
                        <input class="form-input" type="text" id="prenom" name="prenom" value="${utilisateur.prenom}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="nom">Nom</label>
                        <input class="form-input" type="text" id="nom" name="nom" value="${utilisateur.nom}" required>
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label" for="email">Adresse e-mail</label>
                    <input class="form-input" type="email" id="email" name="email" value="${utilisateur.email}" required>
                </div>
            </div>
            </form>
        </div>
        <p id="erreur-infos" class="form-error hidden"></p>

        <!-- Accordéon mot de passe -->
        <div class="mdp-toggle" id="mdp-toggle" role="button" aria-expanded="false">
            <span class="mdp-toggle-label">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                Changer le mot de passe
            </span>
            <svg class="mdp-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <polyline points="6 9 12 15 18 9"/>
            </svg>
        </div>
        <form id="form-mdp">
        <div class="mdp-form" id="mdp-form">
            <div class="form-group">
                <label class="form-label" for="mdp-actuel">Mot de passe actuel</label>
                <input class="form-input" type="password" id="mdp-actuel" name="mdpActuel" placeholder="••••••••" required>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label" for="mdp-nouveau">Nouveau mot de passe</label>
                    <input class="form-input" type="password" id="mdp-nouveau" name="mdpNouveau" placeholder="••••••••" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="mdp-confirm">Confirmer</label>
                    <input class="form-input" type="password" id="mdp-confirm" name="mdpConfirm" placeholder="••••••••" required>
                </div>
            </div>
        </div>
        </form>
        <p id="erreur-mdp" class="form-error hidden"></p>

        <!-- Bouton enregistrer -->
        <button type="submit" id="btn-enregistrer" class="btn btn-primary btn-full hidden">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                <polyline points="17 21 17 13 7 13 7 21"/>
                <polyline points="7 3 7 8 15 8"/>
            </svg>
            Enregistrer les modifications
        </button>

    </div>

</main>

<script src="/js/client/commun.js"></script>
<script src="/js/client/profil.js"></script>

</body>
</html>
