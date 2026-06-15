# ElectronVert — Application de facturation

Application web Java EE de gestion de facturation d'énergie électrique.

## Stack technique

- Java 21 — Servlets (javax.servlet 4.0)
- FreeMarker — moteur de templates
- JDBC — accès base de données
- MySQL — base de données
- BCrypt (jBCrypt) — hashage des mots de passe
- Flying Saucer + OpenPDF — génération de PDF
- Tomcat — serveur d'application

## Prérequis

- XAMPP (MySQL + phpMyAdmin)
- JDK 21
- Maven
- Tomcat 10

## Installation

1. Cloner le dépôt
2. Créer la base de données `electronvert` dans phpMyAdmin
3. Importer `src/main/resources/schema.sql` pour créer les tables
4. Importer `src/main/resources/data.sql` pour les données de test
5. Créer le fichier `src/main/resources/db.properties` (non versionné) :
```properties
db.url=jdbc:mysql://localhost:3306/electronvert
db.user=root
db.password=
```
6. Builder avec Maven et déployer sur Tomcat
7. Accéder à l'application sur `http://localhost:[port]/ElectronVert` (port 8080 par défaut, selon la configuration Tomcat)

## Compte de test

| Champ    | Valeur                |
|----------|-----------------------|
| Email    | sophie.martin@demo.fr |
| Mot de passe | 123                   |

## Données de test incluses

- **3 contrats** : Classique/Réel (actif), HP-HC/Réel (actif), Classique/Échéancier (clôturé)
- **Factures** : payées, émises (à payer), impayée avec 2 frais de relance
- **Relevés de consommation** pour chaque contrat
- **3 tarifs** historiques (2024, 2025, 2026)

## État d'avancement

### Interface client — complète
- Tableau de bord (contrats actifs, factures récentes, total dû)
- Mes contrats (détail, changement d'offre et de mode de facturation)
- Mes factures (liste, paiement, frais de relance, export PDF)
- Tarifs (tarif en vigueur, historique, recherche par date)
- Mon profil (modification des informations, changement de mot de passe)

### Interface admin — en cours
    
