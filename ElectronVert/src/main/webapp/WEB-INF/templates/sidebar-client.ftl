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
        <a href="/client/dashboard" class="nav-item ${(pageActive == "dashboard")?then("active", "")}" ${(pageActive == "dashboard")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            Tableau de bord
        </a>
        <a href="/client/contrats" class="nav-item ${(pageActive == "contrats")?then("active", "")}" ${(pageActive == "contrats")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            Mes contrats
        </a>
        <a href="/client/factures" class="nav-item ${(pageActive == "factures")?then("active", "")}" ${(pageActive == "factures")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
            Mes factures
        </a>
        <a href="/client/consommation" class="nav-item ${(pageActive == "consommation")?then("active", "")}" ${(pageActive == "consommation")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
            Ma consommation
        </a>
        <div class="nav-section-label">Compte</div>
        <a href="/client/profil" class="nav-item ${(pageActive == "profil")?then("active", "")}" ${(pageActive == "profil")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            Mon profil
        </a>
        <a href="/client/tarifs" class="nav-item ${(pageActive == "tarifs")?then("active", "")}" ${(pageActive == "tarifs")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            Tarifs en vigueur
        </a>
        <a href="/deconnexion" class="nav-item danger">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            Se déconnecter
        </a>
    </nav>
    <div class="sidebar-user">
        <div class="user-avatar" aria-hidden="true">${utilisateur.prenom[0]}${utilisateur.nom[0]}</div>
        <div>
            <div class="user-name">${utilisateur.prenom} ${utilisateur.nom}</div>
            <div class="user-role">Client</div>
        </div>
    </div>
</aside>