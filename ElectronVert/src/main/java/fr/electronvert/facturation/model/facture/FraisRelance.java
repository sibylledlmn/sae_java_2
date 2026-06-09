package fr.electronvert.facturation.model.facture;

import java.time.LocalDate;

/**
 * Représente des frais de relance appliqués à une facture impayée.
 * <p>
 * Les frais de relance sont ajoutés lorsqu'une facture n'est pas réglée
 * après sa date d'échéance. Chaque relance possède un numéro, une date
 * et un montant fixe défini par les règles métier.
 * </p>
 */
public class FraisRelance {

    // =====================
    // CONSTANTES
    // =====================

    /**
     * Montant hors taxes des frais de relance.
     */
    public static final double MONTANT_HT = 15.0;

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Date à laquelle la relance est effectuée.
     */
    private final LocalDate dateRelance;

    /**
     * Montant hors taxes des frais.
     */
    private final double montantHT;

    /**
     * Montant de la TVA appliquée aux frais.
     */
    private final double montantTVA;

    /**
     * Montant toutes taxes comprises des frais.
     */
    private final double montantTTC;

    /**
     * Numéro de la relance (1ère, 2ème, etc.).
     */
    private final int numeroRelance;

    /**
     * Libellé descriptif des frais de relance.
     */
    private final String libelle;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit des frais de relance pour une facture impayée.
     *
     * @param numeroRelance numéro de la relance
     * @param dateRelance date de la relance
     *
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public FraisRelance(int numeroRelance, LocalDate dateRelance) {

        if (numeroRelance <= 0) {
            throw new IllegalArgumentException("Le numéro de relance doit être positif");
        }
        if (dateRelance == null) {
            throw new IllegalArgumentException("La date de relance ne peut pas être nulle");
        }

        this.numeroRelance = numeroRelance;
        this.dateRelance = dateRelance;
        this.montantHT = MONTANT_HT;
        this.montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);
        this.montantTTC = montantHT + montantTVA;
        this.libelle = "Frais de relance n°" + numeroRelance;
    }

    // =====================
    // GETTERS
    // =====================

    /**
     * Retourne le montant TTC des frais de relance.
     *
     * @return montant TTC
     */
    public double getMontantTTC() {
        return montantTTC;
    }

    /**
     * Retourne la date de la relance.
     *
     * @return date de relance
     */
    public LocalDate getDateRelance() {
        return dateRelance;
    }

    /**
     * Retourne le montant HT des frais.
     *
     * @return montant HT
     */
    public double getMontantHT() {
        return montantHT;
    }

    /**
     * Retourne le montant de la TVA appliquée aux frais.
     *
     * @return montant de la TVA
     */
    public double getMontantTVA() {
        return montantTVA;
    }

    /**
     * Retourne le numéro de la relance.
     *
     * @return numéro de relance
     */
    public int getNumeroRelance() {
        return numeroRelance;
    }

    /**
     * Retourne le libellé des frais de relance.
     *
     * @return libellé
     */
    public String getLibelle() {
        return libelle;
    }

    // =====================
    // UTILITAIRES
    // =====================

    /**
     * Retourne une représentation textuelle des frais de relance,
     * utilisée pour l'affichage.
     *
     * @return description des frais de relance
     */
    @Override
    public String toString() {
        return libelle + " - " + String.format("%.2f €", montantTTC);
    }
}
