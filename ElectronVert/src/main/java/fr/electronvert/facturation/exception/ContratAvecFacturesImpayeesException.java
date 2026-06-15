package fr.electronvert.facturation.exception;

import fr.electronvert.facturation.model.facture.Facture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception levée lors d'une tentative de clôture d'un contrat
 * qui possède encore des factures impayées.
 * <p>
 * Cette exception empêche la clôture d'un contrat tant que toutes
 * les factures n'ont pas été réglées par le client.
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class ContratAvecFacturesImpayeesException extends RuntimeException {

    private final String referenceContrat;
    private final List<Facture> facturesImpayees;

    /**
     * Constructeur de l'exception.
     *
     * @param referenceContrat la référence du contrat
     * @param facturesImpayees la liste des factures impayées
     */
    public ContratAvecFacturesImpayeesException(
            String referenceContrat,
            List<Facture> facturesImpayees
    ) {
        super(construireMessage(referenceContrat, facturesImpayees));
        this.referenceContrat = referenceContrat;
        this.facturesImpayees = new ArrayList<>(facturesImpayees);
    }

    private static String construireMessage(String referenceContrat, List<Facture> factures) {
        double montantTotal = factures.stream()
                .mapToDouble(Facture::getMontantTTC)
                .sum();

        return String.format(
                "Impossible de clôturer le contrat %s : %d facture(s) impayée(s) pour un total de %.2f €",
                referenceContrat,
                factures.size(),
                montantTotal
        );
    }

}