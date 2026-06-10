package fr.electronvert.facturation.model.facture;

import java.time.LocalDate;

/**
 * Représente un paiement effectué pour une facture.
 * <p>
 * Un paiement est caractérisé par :
 * <ul>
 *     <li>un identifiant unique</li>
 *     <li>une date de paiement</li>
 *     <li>un montant payé</li>
 * </ul>
 * Les paiements sont immuables après leur création.
 *
 */
public class Paiement {

    // =====================
    // IDENTIFIANT
    // =====================

    private int id;

    // =====================
    // DONNÉES DU PAIEMENT
    // =====================

    /**
     * Date à laquelle le paiement a été effectué.
     */
    private final LocalDate datePaiement;

    /**
     * Montant payé lors du paiement.
     */
    private final double montantPaye;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un paiement avec une date et un montant.
     *
     * @param datePaiement date du paiement
     * @param montantPaye montant payé
     *
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public Paiement(LocalDate datePaiement, double montantPaye) {
        if (datePaiement == null) {
            throw new IllegalArgumentException("La date de paiement ne peut pas être nulle");
        }
        if (montantPaye <= 0) {
            throw new IllegalArgumentException("Le montant payé doit être positif");
        }

        this.datePaiement = datePaiement;
        this.montantPaye = montantPaye;
    }

    // Constructeur pour reconstruction depuis la BDD
    public Paiement(int id, LocalDate datePaiement, double montantPaye) {
        this.id = id;
        this.datePaiement = datePaiement;
        this.montantPaye = montantPaye;
    }



    // =====================
    // GETTERS
    // =====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    /**
     * Retourne la date du paiement.
     *
     * @return date du paiement
     */
    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    /**
     * Retourne le montant payé.
     *
     * @return montant payé
     */
    public double getMontantPaye() {
        return montantPaye;
    }

    // =====================
    // AFFICHAGE
    // =====================

    /**
     * Retourne une description détaillée du paiement.
     *
     * @return détails du paiement
     */
    public String getDetails() {
        StringBuilder sb = new StringBuilder();

        sb.append("Référence du paiement : PAIE-").append(String.format("%04d", id)).append("\n");
        sb.append("Date du paiement : ").append(datePaiement).append("\n");
        sb.append("Montant payé : ")
                .append(String.format("%.2f €", montantPaye))
                .append("\n");

        return sb.toString();
    }

    /**
     * Retourne une description du paiement,
     * utilisée pour l'affichage en liste.
     *
     * @return résumé du paiement
     */
    @Override
    public String toString() {
        return String.format("PAIE-%04d", id) + " - " + datePaiement + " - "
                + String.format("%.2f €", montantPaye);
    }
}
