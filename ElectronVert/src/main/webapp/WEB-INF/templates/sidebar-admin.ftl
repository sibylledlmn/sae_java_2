<aside class="sidebar" aria-label="Navigation principale">
    <div class="sidebar-logo">
        <div class="sidebar-logo-name">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#3ec97a" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
            ElectronVert
        </div>
        <div class="sidebar-logo-sub">Administration</div>
    </div>
    <nav class="sidebar-nav">
        <div class="nav-section-label">Menu</div>
        <a href="/admin/dashboard" class="nav-item ${(pageActive == "dashboard")?then("active", "")}" ${(pageActive == "dashboard")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            Tableau de bord
        </a>
        <a href="/admin/clients" class="nav-item ${(pageActive == "clients")?then("active", "")}" ${(pageActive == "clients")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            Clients
        </a>
        <a href="/admin/contrats" class="nav-item ${(pageActive == "contrats")?then("active", "")}" ${(pageActive == "contrats")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            Contrats
        </a>
        <a href="/admin/factures" class="nav-item ${(pageActive == "factures")?then("active", "")}" ${(pageActive == "factures")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
            Factures
        </a>
        <a href="/admin/releves" class="nav-item ${(pageActive == "releves")?then("active", "")}" ${(pageActive == "releves")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 20h.01"/><path d="M7 20v-4"/><path d="M12 20v-8"/><path d="M17 20v-12"/><path d="M22 4v16"/></svg>
            Relevés
        </a>
        <div class="nav-section-label">Paramètres</div>
        <a href="/admin/tarifs" class="nav-item ${(pageActive == "tarifs")?then("active", "")}" ${(pageActive == "tarifs")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/></svg>
            Tarifs
        </a>
        <a href="/admin/statistiques" class="nav-item ${(pageActive == "statistiques")?then("active", "")}" ${(pageActive == "statistiques")?then("aria-current=\"page\"", "")}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
            Statistiques
        </a>
        <a href="/deconnexion" class="nav-item danger">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            Se déconnecter
        </a>
    </nav>
    <div class="sidebar-user">
        <div class="user-avatar admin" aria-hidden="true">${administrateur.prenom[0]}${administrateur.nom[0]}</div>
        <div>
            <div class="user-name">${administrateur.prenom} ${administrateur.nom}</div>
            <div class="user-role">Administrateur</div>
        </div>
    </div>
</aside>
