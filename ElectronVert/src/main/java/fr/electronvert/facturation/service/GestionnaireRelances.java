package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.FraisRelance;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.facture.TauxTVA;

import java.time.LocalDate;
import java.util.List;

/**
 * Service métier responsable de la génération des frais de relance
 * pour les factures impayées.
 * <p>
 * Cette classe est responsable de :
 * <ul>
 *   <li>Le traitement quotidien des relances pour factures impayées</li>
 *   <li>La création et l'ajout de frais de relance aux factures</li>
 *   <li>La planification automatique des prochaines relances</li>
 * </ul>
 * Les frais de relance sont ajoutés automatiquement aux factures impayées
 * selon un calendrier défini, augmentant ainsi le montant dû par le client.
 *
 * @see FraisRelance
 * @see Facture
 * @see SimulateurDate
 */
public class GestionnaireRelances {

    // =====================
    // TRAITEMENT DES RELANCES
    // =====================

    /**
     * Parcourt les factures impayées et génère des frais de relance
     * si la date de relance est atteinte.
     * <p>
     * Cette méthode est appelée quotidiennement par le {@link SimulateurDate}.
     * Pour chaque facture impayée, vérifie si la date de prochaine relance
     * est atteinte ou dépassée, et génère les frais correspondants.
     * </p>
     * <p>
     * Les relances sont planifiées automatiquement :
     * <ul>
     *   <li>1ère relance : 3 semaines après l'échéance</li>
     *   <li>2ème relance : 3 semaines après la 1ère relance</li>
     *   <li>3ème relance : 3 semaines après la 2ème relance</li>
     *   <li>etc.</li>
     * </ul>
     *
     *
     * @param facturesImpayees liste des factures impayées à traiter
     * @param dateDuJour date courante pour la vérification
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public void traiterRelances(
            List<Facture> facturesImpayees,
            LocalDate dateDuJour
    ) {
        if (facturesImpayees == null || dateDuJour == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        for (Facture facture : facturesImpayees) {
            if (facture.getDateProchaineRelance() != null
                    && !dateDuJour.isBefore(facture.getDateProchaineRelance())) {

                creerFraisRelance(facture, dateDuJour);
            }
        }
    }

    // =====================
    // CRÉATION DE FRAIS DE RELANCE
    // =====================

    /**
     * Crée un frais de relance pour une facture impayée.
     * <p>
     * Un frais de relance comprend :
     * <ul>
     *   <li>Un numéro de relance incrémental (1, 2, 3, etc.)</li>
     *   <li>Un montant fixe HT ({@link FraisRelance#MONTANT_HT})</li>
     *   <li>La TVA applicable</li>
     *   <li>Une date de génération</li>
     * </ul>
     * Le frais est automatiquement ajouté à la facture, augmentant ainsi
     * le montant total à payer. Une nouvelle date de relance est planifiée
     * 3 semaines après la date actuelle.
     *
     *
     * @param facture facture impayée pour laquelle créer le frais
     * @param dateRelance date de génération du frais de relance
     * @return frais de relance créé et ajouté à la facture
     *
     * @throws IllegalArgumentException si la facture est null
     * @throws IllegalStateException si la facture n'est pas impayée
     */
    public FraisRelance creerFraisRelance(
            Facture facture,
            LocalDate dateRelance
    ) {
        if (facture == null) {
            throw new IllegalArgumentException("La facture ne peut pas être nulle");
        }

        if (dateRelance == null) {
            throw new IllegalArgumentException("La date de relance ne peut pas être nulle");
        }

        if (facture.getStatut() != StatutFacture.IMPAYEE) {
            throw new IllegalStateException("La facture n'est pas impayée");
        }

        // Calcul du numéro de relance (incrémental)
        int numeroRelance = facture.getFraisDeRelance().size() + 1;

        // Calcul des montants
        double montantHT = FraisRelance.MONTANT_HT;
        double montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);
        double montantTTC = montantHT + montantTVA;

        // Création du frais de relance
        FraisRelance frais = new FraisRelance(
                numeroRelance,
                dateRelance
        );

        // Ajout à la facture
        facture.ajouterFraisDeRelance(frais);

        // Planification de la prochaine relance (dans 30 jours)
        facture.planifierProchaineRelance(dateRelance);

        return frais;
    }
}

