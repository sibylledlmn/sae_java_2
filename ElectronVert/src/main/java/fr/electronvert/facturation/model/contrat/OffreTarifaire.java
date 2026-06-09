package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.Map;

/**
 * Classe abstraite représentant une offre tarifaire d'électricité.
 * <p>
 * Une offre tarifaire définit :
 * <ul>
 *     <li>le mode de calcul du coût de l'électricité</li>
 *     <li>le calcul du cout de l'abonnement</li>
 *     <li>la manière d'interpréter les relevés de consommation</li>
 * </ul>
 * Les classes concrètes (ex : offre classique, HP/HC) implémentent
 * les règles spécifiques de calcul.
 *
 */
public abstract class OffreTarifaire {

    // =====================
    // CONSTANTES
    // =====================

    /**
     * Frais hors taxes appliqués lors d'un changement d'offre.
     */
    protected static final double FRAIS_CHANGEMENT_OFFRE_HT = 75.0;

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Nom de l'offre tarifaire.
     */
    protected String nom;

    /**
     * Type de consommation associé à l'offre.
     */
    private TypeConso typeConso;

    // =====================
    // MÉTHODES ABSTRAITES
    // =====================

    /**
     * Calcule le coût de l'électricité consommée.
     *
     * @param consommations consommations par type
     * @param tarif tarif en vigueur
     * @return coût total de l'électricité
     */
    public abstract double calculerCoutElectricite(
            Map<TypeConso, Double> consommations,
            Tarif tarif
    );

    /**
     * Calcule le coût annuel de l'abonnement.
     *
     * @param tarif tarif en vigueur
     * @return coût annuel de l'abonnement
     */
    public abstract double calculerCoutAbonnementAnnuel(Tarif tarif);

    /**
     * Calcule le coût mensuel de l'abonnement.
     *
     * @param tarif tarif en vigueur
     * @return coût mensuel de l'abonnement
     */
    public abstract double calculerCoutAbonnementMensuel(Tarif tarif);

    /**
     * Calcule la consommation entre deux relevés.
     *
     * @param precedent relevé précédent
     * @param courant relevé courant
     * @return consommation par type
     */
    public abstract Map<TypeConso, Double> calculerConsommation(
            Releve precedent,
            Releve courant
    );

    // =====================
    // GETTERS
    // =====================

    /**
     * Retourne le type de consommation associé à l'offre.
     *
     * @return type de consommation
     */
    public TypeConso getTypeConso() {
        return typeConso;
    }

    /**
     * Retourne le nom de l'offre tarifaire.
     *
     * @return nom de l'offre
     */
    public String getNom() {
        return nom;
    }

    // =====================
    // MÉTHODES UTILITAIRES
    // =====================

    /**
     * Vérifie la cohérence de deux relevés.
     * <p>
     * Les relevés doivent exister et être chronologiquement valides.
     * </p>
     *
     * @param precedent relevé précédent
     * @param actuel relevé actuel
     *
     * @throws IllegalArgumentException si les relevés sont invalides
     */
    protected void verifierReleves(Releve precedent, Releve actuel) {

        if (precedent == null || actuel == null) {
            throw new IllegalArgumentException("Relevés manquants");
        }

        if (actuel.getDateDeReleve().isBefore(precedent.getDateDeReleve())) {
            throw new IllegalArgumentException("Relevés non chronologiques");
        }
    }

    // =====================
    // MÉTHODES STATIQUES
    // =====================

    /**
     * Retourne les frais hors taxes appliqués lors d'un changement d'offre.
     *
     * @return frais de changement d'offre HT
     */
    public static double getFraisChangementOffreHT() {
        return FRAIS_CHANGEMENT_OFFRE_HT;
    }
}
