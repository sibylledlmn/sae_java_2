package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.Echeancier;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.Paiement;
import fr.electronvert.facturation.model.facture.StatutFacture;

import java.time.LocalDate;
import java.util.*;

/**
 * Service de gestion des paiements.
 * <p>
 * Cette classe est responsable de :
 * <ul>
 *   <li>L'enregistrement des paiements de factures</li>
 *   <li>Le prélèvement automatique des mensualités d'échéancier</li>
 *   <li>La gestion du solde créditeur lors des paiements</li>
 *   <li>La fourniture de statistiques globales (chiffre d'affaires)</li>
 *   <li>L'accès aux paiements par contrat</li>
 * </ul>
 * Les paiements sont enregistrés de manière centralisée et peuvent provenir
 * soit du paiement de factures, soit du prélèvement de mensualités.
 *
 * @see Paiement
 * @see Facture
 * @see Echeancier
 */
public class GestionnairePaiements {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Liste de tous les paiements enregistrés dans le système.
     * Inclut les paiements de factures et les mensualités d'échéancier.
     */
    private final List<Paiement> paiements = new ArrayList<>();

    // =====================
    // PAIEMENT DE FACTURES
    // =====================

    /**
     * Règle une facture en tenant compte du solde créditeur éventuel du contrat.
     * <p>
     * Le comportement est le suivant :
     * <ul>
     *   <li>Si le solde créditeur couvre entièrement le montant de la facture,
     *       la facture est marquée comme payée sans création de paiement.</li>
     *   <li>Si le solde créditeur couvre partiellement la facture,
     *       le solde est consommé et un paiement est créé pour le montant restant.</li>
     *   <li>Si aucun solde créditeur n'est disponible,
     *       un paiement est créé pour le montant total de la facture.</li>
     * </ul>
     *
     * @param facture facture à régler
     * @param datePaiement date du paiement
     * @return le paiement créé, ou {@code null} si la facture a été réglée
     *         intégralement par le solde créditeur
     *
     * @throws IllegalArgumentException si la facture ou la date sont nulles
     * @throws IllegalStateException si la facture est déjà payée
     */
    public Paiement payerFacture(Facture facture, LocalDate datePaiement) {

        if (facture == null || datePaiement == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (facture.getStatut() == StatutFacture.PAYEE) {
            throw new IllegalStateException("La facture est déjà payée");
        }

        Contrat contrat = facture.getContrat();

        double montantRestant = facture.getMontantTotalTTCAPayer();
        double soldeCrediteur = contrat.getSoldeCrediteur(); // négatif = crédit

        // Cas 1 : le solde couvre entièrement la facture
        if (soldeCrediteur < 0 && -soldeCrediteur >= montantRestant) {

            // On réduit le solde créditeur
            contrat.ajouterSoldeCrediteur(montantRestant);

            // La facture est réglée automatiquement
            facture.marquerCommePayee();

            return null; // Aucun paiement créé
        }

        // Cas 2 : le solde couvre partiellement la facture
        if (soldeCrediteur < 0) {
            montantRestant += soldeCrediteur; // solde négatif
            contrat.reinitialiserSoldeCrediteur();
        }

        // Cas 3 : paiement classique
        Paiement paiement = new Paiement(datePaiement, montantRestant);

        facture.enregistrerPaiement(paiement);
        paiements.add(paiement);

        return paiement;
    }




    // =====================
    // PRÉLÈVEMENT DE MENSUALITÉS
    // =====================

    /**
     * Prélève une mensualité dans le cadre d'un contrat en mode échéancier.
     * <p>
     * Cette méthode applique les règles suivantes :
     * <ul>
     *   <li>La mensualité est <strong>toujours comptabilisée</strong> dans l'échéancier</li>
     *   <li>Le <strong>solde créditeur est utilisé en priorité</strong></li>
     *   <li>Un {@link Paiement} n'est créé <strong>que si le montant à payer est strictement positif</strong></li>
     * </ul>
     *<p>
     * Gestion du solde créditeur
     * <ul>
     *   <li>Si le solde créditeur couvre entièrement la mensualité :
     *     <ul>
     *       <li>aucun paiement n'est créé</li>
     *       <li>le solde créditeur est diminué du montant de la mensualité</li>
     *       <li>la mensualité est comptabilisée</li>
     *     </ul>
     *   </li>
     *   <li>Si le solde créditeur couvre partiellement la mensualité :
     *     <ul>
     *       <li>un paiement partiel est créé</li>
     *       <li>le solde créditeur est réinitialisé</li>
     *       <li>la mensualité est comptabilisée</li>
     *     </ul>
     *   </li>
     *   <li>Si aucun solde créditeur n'existe :
     *     <ul>
     *       <li>un paiement du montant complet est créé</li>
     *       <li>la mensualité est comptabilisée</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * Cette méthode garantit qu'aucun paiement de montant nul ou négatif
     * n'est jamais créé.
     *
     * @param contrat contrat concerné par le prélèvement
     * @param datePrelevement date du prélèvement de la mensualité
     * @return le paiement créé, ou {@code null} si la mensualité a été entièrement
     *         couverte par le solde créditeur
     *
     * @throws IllegalArgumentException si un paramètre est {@code null}
     * @throws IllegalStateException si le contrat n'est pas en mode échéancier,
     *         s'il n'existe aucun échéancier associé, ou si aucune mensualité
     *         ne peut être émise
     *
     * @see Echeancier
     * @see Paiement
     * @see Contrat#getSoldeCrediteur()
     */

    public Paiement preleverMensualite(
            Contrat contrat,
            LocalDate datePrelevement
    ) {
        if (contrat == null || datePrelevement == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (contrat.getModeFacturation() != ModeFacturation.ECHEANCIER) {
            throw new IllegalStateException(
                    "Le contrat n'est pas en mode échéancier"
            );
        }

        Echeancier echeancier = contrat.getEcheancier();
        if (echeancier == null) {
            throw new IllegalStateException(
                    "Aucun échéancier associé au contrat"
            );
        }

        if (!echeancier.peutEmettreMensualite()) {
            throw new IllegalStateException(
                    "Aucune mensualité ne peut être émise pour cet échéancier"
            );
        }

        double mensualite = echeancier.getMontantMensualiteTTC();
        double solde = contrat.getSoldeCrediteur();

        Paiement paiement = null;

        // Cas 1 : le solde couvre entièrement la mensualité
        if (-solde >= mensualite) {
            double nouveauSolde = solde + mensualite;
            contrat.ajouterSoldeCrediteur(nouveauSolde);
            echeancier.enregistrerMensualiteEmise();
            return null; // pas de paiement
        }

        //  Cas 2 : solde qui couvre partiellement le montant de la mensualité
        if (solde < 0) {
            double montantAPayer = mensualite + solde; // solde est négatif
            contrat.reinitialiserSoldeCrediteur();

            paiement = new Paiement(datePrelevement, montantAPayer);
        }
        //  Cas 3 : pas de solde créditeur
        else {
            paiement = new Paiement(datePrelevement, mensualite);
        }

        // Comptabilisation de la mensualité
        echeancier.enregistrerMensualiteEmise();

        if (paiement != null) {
            echeancier.ajouterPaiementMensualite(paiement);
            paiements.add(paiement);
        }

        return paiement;
    }


    // =====================
    // ACCÈS AUX DONNÉES
    // =====================

    /**
     * Retourne la liste de tous les paiements enregistrés.
     *
     * @return liste non modifiable des paiements
     */
    public List<Paiement> getPaiements() {
        return Collections.unmodifiableList(paiements);
    }

    /**
     * Retourne tous les paiements associés à un contrat.
     * <p>
     * Inclut :
     * <ul>
     *   <li>Les paiements de factures</li>
     *   <li>Les mensualités prélevées des échéanciers</li>
     * </ul>
     * La liste est triée par date décroissante (plus récent en premier).
     *
     *
     * @param contrat contrat dont récupérer les paiements
     * @return liste triée des paiements du contrat
     *
     * @throws IllegalArgumentException si le contrat est null
     */
    public List<Paiement> getPaiementsPourContrat(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        List<Paiement> paiementsContrat = new ArrayList<>();

        // 1. Paiements issus des factures (uniquement ceux qui existent)
        paiementsContrat.addAll(
                contrat.getFactures().stream()
                        .filter(f -> f.getStatut() == StatutFacture.PAYEE)
                        .map(Facture::getPaiement)
                        .filter(Objects::nonNull) // ✅ CORRECTION CLÉ
                        .toList()
        );

        // 2. Paiements des mensualités
        for (Echeancier echeancier : contrat.getEcheanciers()) {
            paiementsContrat.addAll(
                    echeancier.getPaiementsMensualite().stream()
                            .filter(Objects::nonNull) // sécurité
                            .toList()
            );
        }

        // 3. Tri par date décroissante (plus récent en premier)
        paiementsContrat.sort(
                Comparator.comparing(Paiement::getDatePaiement).reversed()
        );

        return paiementsContrat;
    }


    // =====================
    // STATISTIQUES
    // =====================

    /**
     * Calcule le chiffre d'affaires total (somme de tous les paiements).
     * <p>
     * Inclut tous les paiements enregistrés dans le système :
     * paiements de factures et mensualités d'échéancier.
     * </p>
     *
     * @return montant total des paiements effectués
     */
    public double calculerChiffreAffaires() {
        double total = 0.0;
        for (Paiement p : paiements) {
            total += p.getMontantPaye();
        }
        return total;
    }
}