package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;

import java.time.LocalDate;

/**
 * Simulateur de progression temporelle pour le système de facturation.
 * <p>
 * Cette classe orchestre automatiquement les opérations métier selon
 * un calendrier défini :
 * <ul>
 *   <li><strong>Quotidien</strong> : vérification des échéances et génération des relances</li>
 *   <li><strong>1er du mois</strong> : application des changements d'offre tarifaire</li>
 *   <li><strong>5 du mois</strong> : génération des factures mensuelles et régularisations</li>
 *   <li><strong>6 du mois</strong> : application des changements de mode de facturation</li>
 *   <li><strong>20 du mois</strong> : prélèvement des mensualités d'échéancier</li>
 *   <li><strong>Dernier jour du mois</strong> : génération des relevés mensuels</li>
 * </ul>
 * Le simulateur s'appuie sur tous les gestionnaires métier pour orchestrer
 * ces opérations de manière cohérente et automatisée.
 *
 * @see GestionnaireContrats
 * @see GestionnaireFactures
 * @see GestionnairePaiements
 * @see GestionnaireRelances
 * @see GestionnaireReleves
 * @see SimulateurIndex
 */
public class SimulateurDate {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Date courante de la simulation.
     */
    private LocalDate dateCourante;

    /**
     * Gestionnaire des contrats.
     */
    private final GestionnaireContrats gestionnaireContrats;

    /**
     * Gestionnaire des relevés de compteur.
     */
    private final GestionnaireReleves gestionnaireReleves;

    /**
     * Gestionnaire des factures.
     */
    private final GestionnaireFactures gestionnaireFactures;

    /**
     * Gestionnaire des paiements.
     */
    private final GestionnairePaiements gestionnairePaiements;

    /**
     * Gestionnaire des relances pour impayés.
     */
    private final GestionnaireRelances gestionnaireRelances;

    /**
     * Simulateur d'index de compteur.
     */
    private final SimulateurIndex simulateurIndex;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un simulateur de date avec tous les gestionnaires métier nécessaires.
     *
     * @param dateInitiale date de départ de la simulation
     * @param gestionnaireContrats gestionnaire des contrats
     * @param gestionnaireReleves gestionnaire des relevés
     * @param gestionnaireFactures gestionnaire des factures
     * @param gestionnairePaiements gestionnaire des paiements
     * @param gestionnaireRelances gestionnaire des relances
     * @param simulateurIndex simulateur d'index de compteur
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public SimulateurDate(
            LocalDate dateInitiale,
            GestionnaireContrats gestionnaireContrats,
            GestionnaireReleves gestionnaireReleves,
            GestionnaireFactures gestionnaireFactures,
            GestionnairePaiements gestionnairePaiements,
            GestionnaireRelances gestionnaireRelances,
            SimulateurIndex simulateurIndex
    ) {
        if (dateInitiale == null || gestionnaireContrats == null
                || gestionnaireReleves == null || gestionnaireFactures == null
                || gestionnairePaiements == null || gestionnaireRelances == null
                || simulateurIndex == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.dateCourante = dateInitiale;
        this.gestionnaireContrats = gestionnaireContrats;
        this.gestionnaireReleves = gestionnaireReleves;
        this.gestionnaireFactures = gestionnaireFactures;
        this.gestionnairePaiements = gestionnairePaiements;
        this.gestionnaireRelances = gestionnaireRelances;
        this.simulateurIndex = simulateurIndex;
    }

    // =====================
    // ACCÈS À LA DATE COURANTE
    // =====================

    /**
     * Retourne la date courante du simulateur.
     *
     * @return date actuelle de la simulation
     */
    public LocalDate getDateCourante() {
        return dateCourante;
    }

    // =====================
    // PROGRESSION TEMPORELLE
    // =====================

    /**
     * Avance la date d'un jour et déclenche les opérations planifiées.
     * <p>
     * Les opérations déclenchées automatiquement sont :
     * <ul>
     *   <li><strong>Tous les jours</strong> : vérification des échéances de factures,
     *       génération des relances pour impayés</li>
     *   <li><strong>1er du mois</strong> : application des changements d'offre tarifaire</li>
     *   <li><strong>5 du mois</strong> : génération des factures mensuelles (mode réel),
     *       génération des factures de régularisation (fin d'échéancier)</li>
     *   <li><strong>6 du mois</strong> : application des changements de mode de facturation</li>
     *   <li><strong>20 du mois</strong> : prélèvement des mensualités (mode échéancier)</li>
     *   <li><strong>Dernier jour du mois</strong> : génération des relevés mensuels</li>
     * </ul>
     */
    public void avancerDate() {
        dateCourante = dateCourante.plusDays(1);

        // ========== Opérations quotidiennes ==========
        gestionnaireFactures.verifierEcheances(dateCourante);
        gestionnaireRelances.traiterRelances(
                gestionnaireFactures.getFacturesImpayees(),
                dateCourante
        );
        genererRegularisations();

        // ========== 1er du mois : changements d'offre ==========
        if (dateCourante.getDayOfMonth() == 1) {
            appliquerChangementsOffre();
        }

        // ========== 5 du mois : factures et régularisations ==========
        if (dateCourante.getDayOfMonth() == 5) {
            genererFacturesMensuelles();

        }

        // ========== 6 du mois : changements de mode ==========
        if (dateCourante.getDayOfMonth() == 6) {
            appliquerChangementsMode();
        }

        // ========== 20 du mois : prélèvements ==========
        if (dateCourante.getDayOfMonth() == 20) {
            preleverMensualites();
        }

        // ========== Fin du mois : relevés ==========
        if (dateCourante.getDayOfMonth() == dateCourante.lengthOfMonth()) {
            genererRelevesMensuels();
        }
    }

    /**
     * Avance la date d'un nombre spécifié de jours.
     * <p>
     * Déclenche toutes les opérations planifiées pour chaque jour traversé.
     * </p>
     *
     * @param nbJours nombre de jours à avancer (doit être positif)
     *
     * @throws IllegalArgumentException si nbJours est négatif ou nul
     */
    public void avancerDeJours(int nbJours) {
        if (nbJours <= 0) {
            throw new IllegalArgumentException("Le nombre de jours doit être positif");
        }

        for (int i = 0; i < nbJours; i++) {
            avancerDate();
        }
    }

    /**
     * Avance la date d'un nombre spécifié de mois.
     * <p>
     * Déclenche toutes les opérations planifiées pour chaque jour traversé
     * jusqu'à atteindre la date cible (date courante + nbMois).
     * </p>
     *
     * @param nbMois nombre de mois à avancer (doit être positif)
     *
     * @throws IllegalArgumentException si nbMois est négatif ou nul
     */
    public void avancerDeMois(int nbMois) {
        if (nbMois <= 0) {
            throw new IllegalArgumentException("Le nombre de mois doit être positif");
        }

        LocalDate dateCible = dateCourante.plusMonths(nbMois);

        while (dateCourante.isBefore(dateCible)) {
            avancerDate();
        }
    }

    /**
     * Avance la date jusqu'à une date cible précise.
     * <p>
     * Déclenche toutes les opérations planifiées pour chaque jour traversé.
     * Il est impossible de revenir en arrière dans le temps.
     * </p>
     *
     * @param dateCible date à atteindre (doit être postérieure ou égale à la date courante)
     *
     * @throws IllegalArgumentException si la date cible est antérieure à la date courante
     */
    public void avancerDatePrecise(LocalDate dateCible) {
        if (dateCible == null) {
            throw new IllegalArgumentException("La date cible ne peut pas être null");
        }

        if (dateCible.isBefore(dateCourante)) {
            throw new IllegalArgumentException("Impossible de revenir en arrière dans le temps");
        }

        while (dateCourante.isBefore(dateCible)) {
            avancerDate();
        }
    }

    // =====================
    // OPÉRATIONS PLANIFIÉES
    // =====================

    /**
     * Applique les changements d'offre tarifaire planifiés pour tous les contrats actifs.
     * <p>
     * Opération déclenchée automatiquement le 1er de chaque mois.
     * </p>
     */
    private void appliquerChangementsOffre() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            contrat.appliquerChangementOffre();
        }
    }

    /**
     * Applique les changements de mode de facturation planifiés pour tous les contrats actifs.
     * <p>
     * Opération déclenchée automatiquement le 6 de chaque mois.
     * Gère la création ou suppression d'échéanciers selon le nouveau mode :
     * <ul>
     *   <li>REEL → ECHEANCIER : crée un nouvel échéancier</li>
     *   <li>ECHEANCIER → REEL : supprime l'échéancier actuel</li>
     * </ul>
     * </p>
     */
    private void appliquerChangementsMode() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            gestionnaireContrats.appliquerChangementsPlanifiesModeFacturation(contrat, dateCourante);
        }
    }

    /**
     * Génère les factures mensuelles pour tous les contrats en mode réel.
     * <p>
     * Opération déclenchée automatiquement le 5 de chaque mois.
     * Seuls les contrats avec le mode de facturation REEL reçoivent une facture mensuelle.
     * </p>
     */
    private void genererFacturesMensuelles() {
        for (Contrat contrat : gestionnaireContrats.getContratsAFacturer()) {
            if (contrat.getModeFacturation() == ModeFacturation.REEL) {
                gestionnaireFactures.creerFactureMensuelle(
                        contrat,
                        dateCourante
                );
            }
        }
    }

    /**
     * Génère les factures de régularisation pour les contrats en fin d'échéancier.
     * <p>
     * Opération déclenchée automatiquement le 5 de chaque mois.
     * Pour chaque contrat en mode ECHEANCIER ayant atteint 11 mensualités :
     * <ul>
     *   <li>Génère une facture de régularisation (coût réel - mensualités payées)</li>
     *   <li>Termine l'échéancier actuel</li>
     *   <li>Crée un nouvel échéancier basé sur la consommation réelle</li>
     * </ul>
     * </p>
     */
    private void genererRegularisations() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            if (contrat.getModeFacturation() == ModeFacturation.ECHEANCIER
                    && contrat.getEcheancier() != null
                    && !contrat.getEcheancier().peutEmettreMensualite()) {

                gestionnaireReleves.genererReleveRegularisation(
                        contrat,
                        dateCourante,
                        simulateurIndex
                );


                gestionnaireFactures.regulariserFinEcheancier(
                        contrat,
                        dateCourante
                );

                // Ne recréer un échéancier que si un changement de mode de facturation n'a pas été demandé
                if (contrat.getModeFacturationFutur() == null
                        || contrat.getModeFacturationFutur() == ModeFacturation.ECHEANCIER) {

                    gestionnaireContrats.creerEcheancier(contrat, dateCourante);
                }

            }
        }
    }

    /**
     * Prélève les mensualités pour tous les contrats en mode échéancier.
     * <p>
     * Opération déclenchée automatiquement le 20 de chaque mois.
     * Seuls les contrats avec le mode de facturation ECHEANCIER sont concernés.
     * </p>
     */
    private void preleverMensualites() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            if (contrat.getModeFacturation() == ModeFacturation.ECHEANCIER) {
                gestionnairePaiements.preleverMensualite(
                        contrat,
                        dateCourante
                );
            }
        }
    }

    /**
     * Génère les relevés mensuels pour tous les contrats actifs.
     * <p>
     * Opération déclenchée automatiquement le dernier jour de chaque mois.
     * Les index sont calculés par le {@link SimulateurIndex} en tenant compte
     * du nombre de jours écoulés et de la saison.
     * </p>
     */
    private void genererRelevesMensuels() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            gestionnaireReleves.genererReleveMensuel(
                    contrat,
                    dateCourante,
                    simulateurIndex
            );
        }
    }
}