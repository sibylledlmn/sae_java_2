package fr.electronvert.facturation.servlet.viewmodel;

import fr.electronvert.facturation.model.facture.StatutFacture;

import java.time.LocalDate;

public class FactureViewModel {
    private final String reference;
    private final LocalDate dateEcheance;
    private final double montantTTC;
    private final StatutFacture statut;
    private final int contratId;
    private final String contratAdresse;

    public FactureViewModel(String reference, LocalDate dateEcheance, double montantTTC,
                            StatutFacture statut, int contratId, String contratAdresse) {
        this.reference = reference;
        this.dateEcheance = dateEcheance;
        this.montantTTC = montantTTC;
        this.statut = statut;
        this.contratId = contratId;
        this.contratAdresse = contratAdresse;
    }

    public String getReference() { return reference; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public double getMontantTTC() { return montantTTC; }
    public StatutFacture getStatut() { return statut; }
    public int getContratId() { return contratId; }
    public String getContratAdresse() { return contratAdresse; }
}
