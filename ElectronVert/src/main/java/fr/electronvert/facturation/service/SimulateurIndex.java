package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Simulateur de progression des index de compteur électrique.
 * <p>
 * Cette classe génère des index de consommation réalistes en tenant compte :
 * <ul>
 *   <li>Du temps écoulé depuis le dernier relevé</li>
 *   <li>De la saisonnalité (consommation plus élevée en hiver)</li>
 *   <li>D'une variation aléatoire pour simuler des comportements différents</li>
 * </ul>
 * Les index sont générés pour chaque type de consommation (TOTAL, HP, HC)
 * selon l'offre tarifaire du contrat.
 *
 * <p>
 * <strong>Modèle de consommation :</strong>
 * <ul>
 *   <li>Consommation de base : 375 kWh/mois (TOTAL) ou 250 HP + 125 HC</li>
 *   <li>Variation aléatoire : ×0.8 à ×1.2</li>
 *   <li>Coefficient saisonnier :
 *     <ul>
 *       <li>Hiver (déc-jan-fév) : ×1.5</li>
 *       <li>Été (juin-juil-août) : ×0.8</li>
 *       <li>Printemps/Automne : ×1.0</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 *
 * @see Releve
 * @see TypeConso
 */
public class SimulateurIndex {

    // =====================
    // CONSTANTES
    // =====================

    /**
     * Index minimum pour l'initialisation d'un compteur.
     */
    private static final double INDEX_MIN = 1_000;

    /**
     * Index maximum pour l'initialisation d'un compteur.
     */
    private static final double INDEX_MAX = 10_000;

    /**
     * Facteur de variation minimum pour la consommation (80% de la base).
     */
    private static final double VARIATION_MIN = 0.8;

    /**
     * Facteur de variation maximum pour la consommation (120% de la base).
     */
    private static final double VARIATION_MAX = 1.2;

    // =====================
    // GÉNÉRATION D'INDEX INITIAUX
    // =====================

    /**
     * Génère des index initiaux non nuls pour un nouveau contrat.
     * <p>
     * Simule un compteur déjà existant avec des valeurs comprises entre
     * {@link #INDEX_MIN} et {@link #INDEX_MAX}. Les index sont générés
     * aléatoirement pour tous les types de consommation (TOTAL, HP, HC).
     * </p>
     * <p>
     * Cette méthode est utilisée lors de la création d'un relevé d'ouverture
     * pour donner des valeurs initiales réalistes au compteur.
     * </p>
     *
     * @param contrat contrat pour lequel générer les index
     * @return map des index initiaux par type de consommation
     *
     * @throws IllegalArgumentException si le contrat est null
     */
    public Map<TypeConso, Double> genererIndexInitial(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        Map<TypeConso, Double> index = new HashMap<>();

        for (TypeConso type : TypeConso.values()) {
            double valeur = INDEX_MIN
                    + Math.random() * (INDEX_MAX - INDEX_MIN);
            index.put(type, valeur);
        }

        return index;
    }

    // =====================
    // CALCUL D'INDEX
    // =====================

    /**
     * Calcule les nouveaux index à partir du dernier relevé.
     * <p>
     * La consommation simulée tient compte :
     * <ul>
     *   <li>Du nombre de jours écoulés depuis le dernier relevé</li>
     *   <li>D'un coefficient saisonnier (plus élevé en hiver)</li>
     *   <li>D'une variation aléatoire (0.8 à 1.2) pour diversifier les profils</li>
     * </ul>
     * Formule : {@code nouvel_index = ancien_index + (conso_base × jours × coef_saison × variation)}
     * Cette méthode est utilisée pour générer les relevés mensuels et de clôture.
     *
     * @param contrat contrat pour lequel calculer les index
     * @param date date du nouveau relevé
     * @return map des nouveaux index par type de consommation
     *
     * @throws IllegalArgumentException si le contrat ou la date est null
     * @throws IllegalStateException si le contrat n'a pas de relevé précédent
     */
    public Map<TypeConso, Double> calculerIndex(
            Contrat contrat,
            LocalDate date
    ) {
        if (contrat == null || date == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        Releve dernier = contrat.getDernierReleve();
        if (dernier == null) {
            throw new IllegalStateException(
                    "Le contrat doit avoir au moins un relevé pour calculer les nouveaux index"
            );
        }

        Map<TypeConso, Double> anciensIndex = dernier.getIndex();

        // Calcul du nombre de jours écoulés
        long jours = nombreDeJoursDepuisDernierReleve(contrat, date);

        // Coefficient saisonnier
        double coefSaison = coefficientSaison(date);

        // Calcul des nouveaux index
        Map<TypeConso, Double> nouveauxIndex = new HashMap<>();

        for (TypeConso type : anciensIndex.keySet()) {
            double consoMensuelle = consommationDeBase(type);
            double consoJournaliere = consoMensuelle / 30.0;

            double consommation =
                    consoJournaliere * jours * coefSaison;

            nouveauxIndex.put(
                    type,
                    anciensIndex.get(type) + consommation
            );
        }

        return nouveauxIndex;
    }

    // =====================
    // MÉTHODES UTILITAIRES
    // =====================

    /**
     * Calcule le nombre de jours écoulés depuis le dernier relevé.
     *
     * @param contrat contrat dont calculer la durée
     * @param date date de référence
     * @return nombre de jours entre le dernier relevé et la date donnée
     */
    private long nombreDeJoursDepuisDernierReleve(
            Contrat contrat,
            LocalDate date
    ) {
        Releve dernier = contrat.getDernierReleve();
        return ChronoUnit.DAYS.between(
                dernier.getDateDeReleve(),
                date
        );
    }

    /**
     * Retourne la consommation mensuelle de base selon le type.
     * <p>
     * Valeurs de base :
     * <ul>
     *   <li>HP : 250 kWh/mois</li>
     *   <li>HC : 125 kWh/mois</li>
     *   <li>TOTAL : 375 kWh/mois</li>
     * </ul>
     * Une variation aléatoire ({@link #VARIATION_MIN} à {@link #VARIATION_MAX})
     * est appliquée pour diversifier les profils de consommation.
     * </p>
     *
     * @param type type de consommation
     * @return consommation mensuelle de base avec variation aléatoire
     */
    private double consommationDeBase(TypeConso type) {
        double base;

        switch (type) {
            case HP:
                base = 250;
                break;
            case HC:
                base = 125;
                break;
            case TOTAL:
                base = 375;
                break;
            default:
                base = 0;
        }

        double variation = VARIATION_MIN
                + Math.random() * (VARIATION_MAX - VARIATION_MIN);

        return base * variation;
    }

    /**
     * Calcule le coefficient multiplicateur selon la saison.
     * <p>
     * Coefficients appliqués :
     * <ul>
     *   <li>Hiver (décembre, janvier, février) : 1.5 (chauffage)</li>
     *   <li>Été (juin, juillet, août) : 0.8 (climatisation modérée)</li>
     *   <li>Printemps / Automne : 1.0 (consommation normale)</li>
     * </ul>
     * </p>
     *
     * @param date date pour laquelle calculer le coefficient
     * @return coefficient saisonnier
     */
    private double coefficientSaison(LocalDate date) {
        int mois = date.getMonthValue();

        if (mois == 12 || mois == 1 || mois == 2) {
            return 1.5; // Hiver
        }
        if (mois >= 6 && mois <= 8) {
            return 0.8; // Été
        }
        return 1.0; // Printemps / Automne
    }
}