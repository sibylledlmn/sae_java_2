package fr.electronvert.facturation.servlet.viewmodel;

import fr.electronvert.facturation.model.facture.StatutFacture;

import java.time.LocalDate;

public record FactureViewModel(
        String reference,
        LocalDate dateEcheance,
        double montantTTC,
        StatutFacture statut,
        String contratId,
        String contratAdresse,
        LocalDate datePaiement
) {}
