package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.ContratInactifException;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;

import java.time.LocalDate;
import java.util.Map;

/**
 * Service de gestion des relevés de compteur.
 * <p>
 * Cette classe est responsable de :
 * <ul>
 *   <li>La génération des relevés d'ouverture (lors de la souscription)</li>
 *   <li>La génération des relevés mensuels (fin de chaque mois)</li>
 *   <li>La génération des relevés de clôture (fin de contrat)</li>
 *   <li>La génération automatique de relevés pour tous les contrats actifs</li>
 * </ul>
 * Les relevés sont créés à l'aide du {@link SimulateurIndex} qui simule
 * l'évolution des index de compteur en tenant compte de la consommation
 * et de la saisonnalité.
 *
 * @see Releve
 * @see SimulateurIndex
 * @see TypeReleve
 */
public class GestionnaireReleves {

    // =====================
    // RELEVÉS D'OUVERTURE
    // =====================

    /**
     * Génère un relevé d'ouverture pour un nouveau contrat.
     * <p>
     * Le relevé est créé avec des index initiaux aléatoires simulant
     * un compteur déjà existant. Ce relevé sert de point de départ
     * pour tous les calculs de consommation ultérieurs.
     * </p>
     * <p>
     * Le relevé d'ouverture est daté de la date de souscription du contrat
     * et est automatiquement ajouté à la liste des relevés du contrat.
     * </p>
     *
     * @param contrat contrat pour lequel générer le relevé d'ouverture
     * @param simulateurIndex simulateur pour générer les index initiaux
     * @return relevé d'ouverture créé et ajouté au contrat
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public Releve genererReleveOuverture(
            Contrat contrat,
            SimulateurIndex simulateurIndex
    ) {
        if (contrat == null || simulateurIndex == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        // Génération des index initiaux aléatoires
        Map<TypeConso, Double> indexInitiaux =
                simulateurIndex.genererIndexInitial(contrat);

        // Création du relevé d'ouverture
        Releve releve = new Releve(
                contrat,
                TypeReleve.OUVERTURE,
                contrat.getDateSouscription(),
                indexInitiaux
        );

        // Ajout au contrat
        contrat.ajouterReleve(releve);

        return releve;
    }

    // =====================
    // RELEVÉS MENSUELS
    // =====================

    /**
     * Génère un relevé mensuel pour un contrat actif.
     * <p>
     * Les nouveaux index sont calculés à partir du dernier relevé
     * en tenant compte :
     * <ul>
     *   <li>Du nombre de jours écoulés depuis le dernier relevé</li>
     *   <li>De la saison (consommation plus élevée en hiver)</li>
     *   <li>D'une variation aléatoire pour simuler différents profils</li>
     * </ul>
     * Le relevé mensuel est automatiquement ajouté au contrat et servira
     * de base pour la facturation mensuelle.
     *
     *
     * @param contrat contrat pour lequel générer le relevé mensuel
     * @param date date du relevé mensuel (généralement fin de mois)
     * @param simulateurIndex simulateur pour calculer les nouveaux index
     * @return relevé mensuel créé et ajouté au contrat
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     * @throws ContratInactifException si le contrat n'est pas actif
     * @throws IllegalStateException si aucun relevé d'ouverture n'existe
     */
    public Releve genererReleveMensuel(
            Contrat contrat,
            LocalDate date,
            SimulateurIndex simulateurIndex
    ) {
        if (contrat == null || date == null || simulateurIndex == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!contrat.estActif()) {
            throw new ContratInactifException(
                    contrat.getReference(),
                    contrat.getDateFin()
            );
        }

        if (contrat.getDernierReleve() == null) {
            throw new IllegalStateException(
                    "Impossible de générer un relevé mensuel sans relevé d'ouverture"
            );
        }

        // Calcul des nouveaux index
        Map<TypeConso, Double> index =
                simulateurIndex.calculerIndex(contrat, date);

        // Création du relevé mensuel
        Releve releve = new Releve(
                contrat,
                TypeReleve.MENSUEL,
                date,
                index
        );

        // Ajout au contrat
        contrat.ajouterReleve(releve);

        return releve;
    }



    // =====================
    // RELEVÉS DE CLÔTURE
    // =====================

    /**
     * Génère un relevé de clôture pour un contrat clôturé.
     * <p>
     * Ce relevé permet de calculer la consommation finale du contrat
     * entre le dernier relevé mensuel et la date de clôture.
     * Il est nécessaire pour établir la facture de régularisation finale.
     * </p>
     * <p>
     * Le relevé de clôture ne peut être généré qu'une seule fois par contrat.
     * </p>
     *
     * @param contrat contrat pour lequel générer le relevé de clôture
     * @param dateCloture date du relevé de clôture
     * @param simulateurIndex simulateur pour calculer les index finaux
     * @return relevé de clôture créé et ajouté au contrat
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     * @throws IllegalStateException si le contrat est encore actif ou
     *         si un relevé de clôture existe déjà
     */
    public Releve genererReleveCloture(
            Contrat contrat,
            LocalDate dateCloture,
            SimulateurIndex simulateurIndex
    ) {
        if (contrat == null || dateCloture == null || simulateurIndex == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (contrat.estActif()) {
            throw new IllegalStateException(
                    "Impossible de générer un relevé de clôture : le contrat n'est pas clôturé"
            );
        }

        // Vérification qu'un relevé de clôture n'existe pas déjà
        Releve dernier = contrat.getDernierReleve();
        if (dernier != null && dernier.getTypeReleve() == TypeReleve.CLOTURE) {
            throw new IllegalStateException(
                    "Un relevé de clôture existe déjà pour ce contrat"
            );
        }

        // Calcul des index finaux
        Map<TypeConso, Double> index =
                simulateurIndex.calculerIndex(contrat, dateCloture);

        // Création du relevé de clôture
        Releve releve = new Releve(
                contrat,
                TypeReleve.CLOTURE,
                dateCloture,
                index
        );

        // Ajout au contrat
        contrat.ajouterReleve(releve);

        return releve;
    }

    // =====================
// RELEVÉS DE RÉGULARISATION
// =====================

    /**
     * Génère un relevé de régularisation pour un contrat en fin d'échéancier.
     * <p>
     * Ce relevé marque la fin de la période couverte par l'échéancier et sert
     * de borne finale pour le calcul de la consommation réelle lors de la
     * facture de régularisation.
     * </p>
     * <p>
     * Le relevé est daté de la date de régularisation (généralement le 5 du mois)
     * et est calculé à partir du dernier relevé existant via le simulateur d'index.
     * </p>
     *
     * @param contrat contrat concerné
     * @param dateRegularisation date du relevé de régularisation
     * @param simulateurIndex simulateur pour calculer les index
     * @return relevé de régularisation créé et ajouté au contrat
     *
     * @throws IllegalArgumentException si un paramètre est null
     * @throws IllegalStateException si aucun relevé préalable n'existe
     */
    public Releve genererReleveRegularisation(
            Contrat contrat,
            LocalDate dateRegularisation,
            SimulateurIndex simulateurIndex
    ) {
        if (contrat == null || dateRegularisation == null || simulateurIndex == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        Releve dernierReleve = contrat.getDernierReleve();
        if (dernierReleve == null) {
            throw new IllegalStateException(
                    "Impossible de générer un relevé de régularisation sans relevé préalable"
            );
        }

        Map<TypeConso, Double> index =
                simulateurIndex.calculerIndex(contrat, dateRegularisation);

        Releve releve = new Releve(
                contrat,
                TypeReleve.REGULARISATION,
                dateRegularisation,
                index
        );

        contrat.ajouterReleve(releve);

        return releve;
    }



}
