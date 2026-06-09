package fr.electronvert.facturation.model.releve;

/**
 * Énumération représentant les différents types de consommation électrique.
 * <p>
 * Le type de consommation dépend de l'offre tarifaire associée au contrat :
 * <ul>
 *     <li>{@code TOTAL} pour une offre classique</li>
 *     <li>{@code HP} et {@code HC} pour une offre Heures Pleines / Heures Creuses</li>
 * </ul>
 *
 */
public enum TypeConso {

    /**
     * Consommation totale, utilisée pour l'offre classique.
     */
    TOTAL,

    /**
     * Consommation en heures pleines.
     */
    HP,

    /**
     * Consommation en heures creuses.
     */
    HC
}
