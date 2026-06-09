package fr.electronvert.facturation.model.contrat;

/**
 * Énumération représentant les différents états possibles d'un contrat.
 * <p>
 * Le statut d'un contrat permet de déterminer s'il est encore en cours
 * ou s'il a été clôturé.
 * </p>
 */
public enum StatutContrat {

    /**
     * Contrat actif, en cours de facturation et de gestion.
     */
    ACTIF,

    /**
     * Contrat clôturé, ne donnant plus lieu à facturation.
     */
    CLOTURE
}
