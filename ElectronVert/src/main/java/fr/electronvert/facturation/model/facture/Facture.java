package fr.electronvert.facturation.model.facture;

import fr.electronvert.facturation.exception.FactureDejaPayeeException;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.TypeReleve;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente une facture figée du système de facturation.
 * <p>
 * Les montants sont calculés en amont par le gestionnaire de facturation
 * et la facture se charge uniquement de stocker ces montants et de gérer
 * son cycle de vie (émission, paiement, impayé, relances).
 * </p>
 */
public class Facture implements Comparable<Facture> {

    // =====================

    public boolean isContientFraisChangementOffre() {
        return contientFraisChangementOffre;
    }
    // IDENTITÉ & LIENS
    // =====================

    private int id;
    private final String reference;
    private final TypeFacture type;
    private final Contrat contrat;
    private int contratId;

    // =====================
    // DATES
    // =====================

    private final LocalDate dateEmission;
    private final LocalDate dateEcheance;
    private LocalDate dateProchaineRelance;

    // =====================
    // MONTANTS
    // =====================

    private double montantHT;
    private double montantTVA;
    private double montantTTC;
    private boolean montantsDefinis = false;

    // =====================
    // ÉTAT & PAIEMENT
    // =====================

    private StatutFacture statut;
    private Paiement paiement;

    // =====================
    // FRAIS & RELANCES
    // =====================

    private final List<FraisRelance> fraisDeRelance = new ArrayList<>();
    private boolean contientFraisChangementOffre = false;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une facture et l'attache automatiquement au contrat.
     *
     * @param contrat contrat concerné
     * @param dateEmission date d'émission de la facture
     * @param reference référence unique de la facture
     * @param type type de facture
     *
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws IllegalStateException si la facture ne peut pas être créée
     */
    public Facture(
            Contrat contrat,
            LocalDate dateEmission,
            String reference,
            TypeFacture type
    ) {
        if (contrat == null) {
            throw new IllegalArgumentException("Une facture doit être liée à un contrat");
        }

        if (contrat.estFacturationTerminee()) {
            throw new IllegalStateException(
                    "La facturation du contrat est déjà terminée"
            );
        }

        if (!contrat.estActif()
                && type != TypeFacture.REGULARISATION
                && !(type == TypeFacture.MENSUELLE
                && contrat.getDernierReleve() != null
                && contrat.getDernierReleve().getTypeReleve() == TypeReleve.CLOTURE)) {

            throw new IllegalStateException(
                    "Impossible de créer une facture pour un contrat clôturé"
            );
        }

        if (dateEmission == null) {
            throw new IllegalArgumentException("La date d'émission ne peut pas être nulle");
        }
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("La référence de la facture est invalide");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type de facture ne peut pas être nul");
        }

        this.contrat = contrat;
        this.dateEmission = dateEmission;
        this.dateEcheance = dateEmission.plusDays(14);
        this.reference = reference;
        this.type = type;
        this.statut = StatutFacture.EMISE;

        contrat.ajouterFacture(this);
    }

    // Constructeur pour reconstruction depuis la BDD
    public Facture(int id, String reference, TypeFacture type,
                   LocalDate dateEmission, LocalDate dateEcheance, LocalDate dateProchaineRelance,
                   double montantHT, double montantTVA, double montantTTC,
                   StatutFacture statut, boolean contientFraisChangementOffre, int contratId) {
        this.id = id;
        this.reference = reference;
        this.type = type;
        this.contrat = null;
        this.contratId = contratId;
        this.dateEmission = dateEmission;
        this.dateEcheance = dateEcheance;
        this.dateProchaineRelance = dateProchaineRelance;
        this.montantHT = montantHT;
        this.montantTVA = montantTVA;
        this.montantTTC = montantTTC;
        this.montantsDefinis = true;
        this.statut = statut;
        this.contientFraisChangementOffre = contientFraisChangementOffre;
    }

    // =====================
    // MÉTHODES MÉTIER
    // =====================

    /**
     * Définit les montants de la facture.
     * <p>
     * Cette méthode ne peut être appelée qu'une seule fois.
     * </p>
     *
     * @param montantHT montant hors taxes
     * @param montantTVA montant de la TVA
     * @param montantTTC montant toutes taxes comprises
     */
    public void definirMontants(double montantHT, double montantTVA, double montantTTC) {
        if (montantsDefinis) {
            throw new IllegalStateException("Les montants sont déjà définis");
        }
        if (montantHT < 0 || montantTVA < 0 || montantTTC < 0) {
            throw new IllegalArgumentException("Les montants ne peuvent pas être négatifs");
        }
        if (Math.abs((montantHT + montantTVA) - montantTTC) > 0.01) {
            throw new IllegalArgumentException("Incohérence entre HT, TVA et TTC");
        }

        this.montantHT = montantHT;
        this.montantTVA = montantTVA;
        this.montantTTC = montantTTC;
        this.montantsDefinis = true;
    }

    /**
     * Marque la facture comme payée.
     */
    public void marquerCommePayee() {
        if (statut == StatutFacture.PAYEE) {
            throw new IllegalStateException("La facture est déjà payée");
        }
        this.statut = StatutFacture.PAYEE;
    }

    /**
     * Passe la facture à l'état impayé.
     *
     * @param datePassageImpayee date de passage en impayé
     */
    public void passerEnImpayee(LocalDate datePassageImpayee) {
        if (statut == StatutFacture.PAYEE || statut == StatutFacture.IMPAYEE) {
            throw new IllegalStateException("Transition d'état invalide");
        }
        this.statut = StatutFacture.IMPAYEE;
        this.dateProchaineRelance = datePassageImpayee;
    }

    /**
     * Planifie la prochaine relance, 3 semaines après la précédente
     *
     * @param dateRelance date de référence
     */
    public void planifierProchaineRelance(LocalDate dateRelance) {
        this.dateProchaineRelance = dateRelance.plusWeeks(3);
    }

    /**
     * Ajoute des frais de relance à la facture.
     *
     * @param frais frais de relance
     */
    public void ajouterFraisDeRelance(FraisRelance frais) {
        if (frais == null) {
            throw new IllegalArgumentException("Le frais de relance ne peut pas être nul");
        }
        this.fraisDeRelance.add(frais);
    }

    /**
     * Enregistre le paiement de la facture.
     *
     * @param paiement paiement effectué
     */
    public void enregistrerPaiement(Paiement paiement) {
        if (paiement == null) {
            throw new IllegalArgumentException("Le paiement ne peut pas être nul");
        }
        if (statut == StatutFacture.PAYEE) {
            throw new FactureDejaPayeeException(
                    reference,
                    this.paiement != null ? this.paiement.getDatePaiement() : null
            );
        }
        this.paiement = paiement;
        this.statut = StatutFacture.PAYEE;
    }

    /**
     * Indique que la facture contient des frais de changement d'offre.
     */
    public void marquerPresenceFraisChangementOffre() {
        this.contientFraisChangementOffre = true;
    }

//    // =====================
//    // CALCULS
//    // =====================
//
//    /**
//     * Calcule le montant total TTC à payer, incluant les frais de relance.
//     *
//     * @return montant total TTC
//     */
//    public double getMontantTotalTTCAPayer() {
//        double total = montantTTC;
//        for (FraisRelance frais : fraisDeRelance) {
//            total += frais.getMontantTTC();
//        }
//        return total;
//    }
//
//    // =====================
//    // AFFICHAGE
//    // =====================
//
//    /**
//     * Résumé court de la facture pour affichage en liste.
//     *
//     * @return résumé de la facture
//     */
//    public String toResume() {
//        return reference + " | "
//                + dateEmission + " | "
//                + arrondir2Decimales(getMontantTotalTTCAPayer()) + " € | "
//                + statut;
//    }
//
//    /**
//     * Retourne les détails complets de la facture.
//     *
//     * @return description détaillée
//     */
//    public String getDetails() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("=== FACTURE ===\n")
//                .append("Référence : ").append(reference).append("\n")
//                .append("Type : ").append(type).append("\n")
//                .append("Date émission : ").append(dateEmission.format(formatter)).append("\n")
//                .append("Date échéance : ").append(dateEcheance.format(formatter)).append("\n")
//                .append("Statut : ").append(statut).append("\n\n")
//
//                .append("--- MONTANTS ---\n")
//                .append("HT : ").append(arrondir2Decimales(montantHT)).append(" €\n")
//                .append("TVA : ").append(arrondir2Decimales(montantTVA)).append(" €\n")
//                .append("TTC : ").append(arrondir2Decimales(montantTTC)).append(" €\n");
//
//        if (!fraisDeRelance.isEmpty()) {
//            sb.append("\n--- FRAIS DE RELANCE ---\n");
//            for (FraisRelance frais : fraisDeRelance) {
//                sb.append(frais).append("\n");
//            }
//            sb.append("\nTOTAL À PAYER : ")
//                    .append(arrondir2Decimales(getMontantTotalTTCAPayer()))
//                    .append(" €\n");
//        }
//
//        if (contientFraisChangementOffre) {
//            sb.append("\n--- INFORMATION ---\n")
//                    .append("Cette facture inclut des frais de changement d'offre.\n");
//        }
//
//        return sb.toString();
//    }

    // =====================
    // GETTERS
    // =====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getContratId() { return contratId; }
    public String getReference() { return reference; }
    public TypeFacture getType() { return type; }
    public Contrat getContrat() { return contrat; }
    public LocalDate getDateEmission() { return dateEmission; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public LocalDate getDateProchaineRelance() { return dateProchaineRelance; }
    public StatutFacture getStatut() { return statut; }

    public List<FraisRelance> getFraisDeRelance() {
        return Collections.unmodifiableList(fraisDeRelance);
    }

    public double getMontantHT() { return montantHT; }
    public double getMontantTVA() { return montantTVA; }
    public double getMontantTTC() { return montantTTC; }

    /**
     * Retourne le paiement associé à la facture.
     *
     * @return le paiement, ou {@code null} si la facture avait un montant de 0€
     * en cas de régularisation ou utilisation d'un solde créditeur
     */
    public Paiement getPaiement() {
        return paiement;
    }


    // =====================
    // UTILITAIRES
    // =====================

    @Override
    public int compareTo(Facture autre) {
        int cmp = this.dateEmission.compareTo(autre.dateEmission);
        return (cmp != 0) ? cmp : this.reference.compareTo(autre.reference);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facture facture)) return false;
        return reference.equals(facture.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    private double arrondir2Decimales(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}
