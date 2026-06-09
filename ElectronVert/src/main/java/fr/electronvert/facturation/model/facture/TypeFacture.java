package fr.electronvert.facturation.model.facture;

/**
 * Énumération représentant les différents types de factures du système.
 * <p>
 * Le type de facture détermine le contexte dans lequel elle est émise
 * et les règles de calcul qui lui sont associées.
 * </p>
 */
public enum TypeFacture {

    /**
     * Facture mensuelle classique, émise périodiquement
     * en fonction de la consommation.
     */
    MENSUELLE,

    /**
     * Facture de régularisation, permettant d'ajuster
     * les montants après une période d'échéancier.
     */
    REGULARISATION,

    /**
     * Facture de clôture, émise lors de la résiliation d'un contrat.
     */
    CLOTURE
}
