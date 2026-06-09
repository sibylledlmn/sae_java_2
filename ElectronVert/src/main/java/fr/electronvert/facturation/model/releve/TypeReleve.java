package fr.electronvert.facturation.model.releve;

/**
 * Énumération représentant les différents types de relevés de consommation.
 * <p>
 * Le type de relevé permet de contextualiser la lecture des index
 * et d'adapter les règles de facturation associées.
 * </p>
 */
public enum TypeReleve {

    /**
     * Relevé d'ouverture, effectué lors de la création du contrat.
     */
    OUVERTURE,

    /**
     * Relevé mensuel, utilisé pour la facturation périodique.
     */
    MENSUEL,

    /**
     * Relevé de clôture, effectué lors de la résiliation du contrat.
     */
    CLOTURE,
    /**
     * Relevé de régularisation, effectué lors de la régularisation annuelle.
     */
    REGULARISATION,

}
