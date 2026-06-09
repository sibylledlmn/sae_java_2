package fr.electronvert.facturation.controller.administrateur;

import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.service.GestionnaireTarifs;
import fr.electronvert.facturation.service.SimulateurDate;
import fr.electronvert.facturation.view.administrateur.VueGestionTarifs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contrôleur de gestion des tarifs pour l'administrateur.
 * <p>
 * Ce contrôleur permet à l'administrateur de :
 * <ul>
 *   <li>Consulter le tarif actuellement en vigueur</li>
 *   <li>Consulter l'historique complet des tarifs</li>
 *   <li>Créer un nouveau tarif </li>
 * </ul>
 * La création d'un tarif nécessite :
 * <ul>
 *   <li>Une date d'entrée en vigueur (1er février ou 1er août)</li>
 *   <li>Les prix du kWh pour les offres Classique et HP/HC</li>
 *   <li>Les prix des abonnements mensuels pour chaque offre</li>
 *   <li>Une confirmation avant l'enregistrement</li>
 * </ul>
 *
 * @see VueGestionTarifs
 * @see GestionnaireTarifs
 * @see SimulateurDate
 */
public class ControleurGestionTarifsAdmin {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de gestion des tarifs.
     */
    private final VueGestionTarifs vue;

    /**
     * Gestionnaire des tarifs pour les opérations métier.
     */
    private final GestionnaireTarifs gestionnaireTarifs;

    /**
     * Simulateur de date pour obtenir la date courante lors des consultations.
     */
    private final SimulateurDate simulateurDate;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de gestion des tarifs.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireTarifs gestionnaire des tarifs
     * @param simulateurDate simulateur de date
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurGestionTarifsAdmin(
            Scanner scanner,
            GestionnaireTarifs gestionnaireTarifs,
            SimulateurDate simulateurDate
    ) {
        if (scanner == null || gestionnaireTarifs == null || simulateurDate == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueGestionTarifs(scanner);
        this.gestionnaireTarifs = gestionnaireTarifs;
        this.simulateurDate = simulateurDate;
    }

    // =====================
    // GESTION DU MENU
    // =====================

    /**
     * Lance le menu de gestion des tarifs.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter le tarif actuel</li>
     *   <li>2 : Consulter l'historique des tarifs</li>
     *   <li>3 : Créer un nouveau tarif</li>
     *   <li>0 : Retour au menu administrateur</li>
     * </ul>
     * Les erreurs (date invalide, paramètres incorrects) sont capturées
     * et affichées à l'utilisateur.
     */
    public void demarrer() {
        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuGestionTarifs();

            try {
                switch (choix) {
                    case 1 -> consulterTarifActuel();
                    case 2 -> consulterHistoriqueTarifs();
                    case 3 -> creerNouveauTarif();
                    case 0 -> retour = true;
                }
            } catch (Exception e) {
                vue.afficherMessage("Erreur : " + e.getMessage());
                vue.attendreEntree();
            }
        }
    }

    // =====================
    // CONSULTATION DES TARIFS
    // =====================

    /**
     * Affiche le tarif actuellement en vigueur.
     * <p>
     * Le tarif actif est déterminé en fonction de la date courante
     * du {@link SimulateurDate}. Il s'agit du tarif le plus récent
     * dont la date de début est antérieure ou égale à la date du jour.
     * </p>
     */
    private void consulterTarifActuel() {
        LocalDate dateCourante = simulateurDate.getDateCourante();
        Tarif tarifActuel = gestionnaireTarifs.getTarifActif(dateCourante);

        vue.afficherTarifActuel(tarifActuel.toString());
    }

    /**
     * Affiche l'historique complet des tarifs.
     * <p>
     * Présente tous les tarifs qui ont été en vigueur, du plus ancien
     * au plus récent. Chaque tarif affiché comprend sa date de début,
     * les prix du kWh et les prix des abonnements.
     * </p>
     */
    private void consulterHistoriqueTarifs() {
        List<Tarif> historique = gestionnaireTarifs.getHistoriqueDesTarifs();

        List<String> tarifsFormates = new ArrayList<>();
        for (Tarif tarif : historique) {
            tarifsFormates.add(tarif.toString());
        }

        vue.afficherHistoriqueTarifs(tarifsFormates);
    }

    // =====================
    // CRÉATION D'UN NOUVEAU TARIF
    // =====================

    /**
     * Procédure de création d'un nouveau tarif.
     * <p>
     * Workflow de création en 6 étapes :
     * <ol>
     *   <li>Demande de la date d'entrée en vigueur (1er février ou 1er août)</li>
     *   <li>Demande des prix du kWh (Classique, HP, HC)</li>
     *   <li>Demande des prix des abonnements mensuels (Classique, HP/HC)</li>
     *   <li>Création d'un tarif temporaire pour prévisualisation</li>
     *   <li>Affichage du récapitulatif et demande de confirmation</li>
     *   <li>Enregistrement du tarif si confirmé, annulation sinon</li>
     * </ol>
     * </p>
     * <p>
     * Contraintes :
     * <ul>
     *   <li>La date doit être un 1er février ou 1er août</li>
     *   <li>La date doit être postérieure au dernier tarif enregistré</li>
     *   <li>Tous les prix doivent être positifs</li>
     * </ul>
     * En cas d'erreur de validation, affiche un message approprié et
     * retourne au menu.
     * </p>
     */
    private void creerNouveauTarif() {
        try {
            // 1. Demander la date d'entrée en vigueur
            LocalDate dateEntreeVigueur = vue.demanderDateEntreeVigueurTarif();

            // 2. Demander les prix du kWh
            double prixKwhClassique = vue.demanderPrixKwhClassique();
            double prixKwhHP = vue.demanderPrixKwhHeuresPleines();
            double prixKwhHC = vue.demanderPrixKwhHeuresCreuses();

            // 3. Demander les prix des abonnements
            double prixAbonnementClassique = vue.demanderPrixAbonnementClassique();
            double prixAbonnementHPHC = vue.demanderPrixAbonnementHPHC();

            // 4. Créer un tarif temporaire pour le récapitulatif
            Tarif nouveauTarif = new Tarif(
                    dateEntreeVigueur,
                    prixKwhClassique,
                    prixKwhHP,
                    prixKwhHC,
                    prixAbonnementClassique,
                    prixAbonnementHPHC
            );

            // 5. Afficher le récapitulatif et demander confirmation
            vue.afficherRecapitulatifTarif(nouveauTarif.toString());

            boolean confirmation = vue.demanderConfirmation(
                    "Confirmez-vous la création de ce tarif ?"
            );

            if (!confirmation) {
                vue.afficherMessage("Création annulée.");
                vue.attendreEntree();
                return;
            }

            // 6. Enregistrer le tarif (peut lever DateChangementTarifInvalideException)
            gestionnaireTarifs.creerNouveauTarif(
                    dateEntreeVigueur,
                    prixKwhClassique,
                    prixKwhHP,
                    prixKwhHC,
                    prixAbonnementClassique,
                    prixAbonnementHPHC
            );

            vue.afficherConfirmationCreationTarif();

        } catch (Exception e) {
            vue.afficherErreur(e.getMessage());
            vue.attendreEntree();
        }
    }
}