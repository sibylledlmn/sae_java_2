package fr.electronvert.facturation.model.tarif;

import java.time.LocalDate;

/**
 * Représente un tarif d'électricité applicable à partir d'une date donnée.
 * <p>
 * Un tarif définit :
 * <ul>
 *     <li>le prix du kilowattheure selon l'offre tarifaire</li>
 *     <li>le prix de l'abonnement mensuel</li>
 * </ul>
 * Les tarifs sont immuables et comparables par date d'entrée en vigueur.
 *
 */
public class Tarif implements Comparable<Tarif> {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Date d'entrée en vigueur du tarif.
     */
    private final LocalDate dateDebut;

    /**
     * Prix du kilowattheure (en euros).
     */
    private final double prixKwhClassique;
    private final double prixKwhHP;
    private final double prixKwhHC;

    /**
     * Prix de l'abonnement mensuel (en euros).
     */
    private final double prixAbonnementClassique;
    private final double prixAbonnementHPHC;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un tarif applicable à partir d'une date donnée.
     *
     * @param dateDebut date d'entrée en vigueur
     * @param prixKwhClassique prix du kWh classique
     * @param prixKwhHeuresPleines prix du kWh heures pleines
     * @param prixKwhHeuresCreuses prix du kWh heures creuses
     * @param prixAbonnementClassique prix de l'abonnement classique
     * @param prixAbonnementHPHC prix de l'abonnement HP/HC
     *
     * @throws IllegalArgumentException si un paramètre est invalide
     */
    public Tarif(
            LocalDate dateDebut,
            double prixKwhClassique,
            double prixKwhHeuresPleines,
            double prixKwhHeuresCreuses,
            double prixAbonnementClassique,
            double prixAbonnementHPHC
    ) {

        if (dateDebut == null) {
            throw new IllegalArgumentException(
                    "La date de début du tarif est obligatoire"
            );
        }

        verifierPrix(prixKwhClassique, "kWh classique");
        verifierPrix(prixKwhHeuresPleines, "kWh heures pleines");
        verifierPrix(prixKwhHeuresCreuses, "kWh heures creuses");
        verifierPrix(prixAbonnementClassique, "abonnement classique");
        verifierPrix(prixAbonnementHPHC, "abonnement HP/HC");

        this.dateDebut = dateDebut;
        this.prixKwhClassique = prixKwhClassique;
        this.prixKwhHP = prixKwhHeuresPleines;
        this.prixKwhHC = prixKwhHeuresCreuses;
        this.prixAbonnementClassique = prixAbonnementClassique;
        this.prixAbonnementHPHC = prixAbonnementHPHC;
    }

    /**
     * Vérifie qu'un prix est strictement positif.
     *
     * @param prix valeur à vérifier
     * @param libelle description du prix
     */
    private void verifierPrix(double prix, String libelle) {
        if (prix <= 0) {
            throw new IllegalArgumentException(
                    "Le prix " + libelle + " doit être supérieur à 0"
            );
        }
    }

    // =====================
    // GETTERS
    // =====================

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public double getPrixKwhClassique() {
        return prixKwhClassique;
    }

    public double getPrixKwhHP() {
        return prixKwhHP;
    }

    public double getPrixKwhHC() {
        return prixKwhHC;
    }

    public double getPrixAbonnementClassique() {
        return prixAbonnementClassique;
    }

    public double getPrixAbonnementHPHC() {
        return prixAbonnementHPHC;
    }

    // =====================
    // UTILITAIRES
    // =====================

    /**
     * Compare deux tarifs selon leur date d'entrée en vigueur.
     *
     * @param autre tarif à comparer
     * @return comparaison par date
     */
    @Override
    public int compareTo(Tarif autre) {
        return this.dateDebut.compareTo(autre.dateDebut);
    }

    /**
     * Affichage détaillé du tarif.
     *
     * @return représentation textuelle du tarif
     */
    @Override
    public String toString() {
        return "Tarif en vigueur à partir du " + dateDebut + "\n"
                + "--------------------------------\n"
                + "Prix du kWh :\n"
                + " - Classique : " + arrondir(prixKwhClassique, 4) + " €\n"
                + " - Heures Pleines : " + arrondir(prixKwhHP, 4) + " €\n"
                + " - Heures Creuses : " + arrondir(prixKwhHC, 4) + " €\n\n"
                + "Abonnement mensuel :\n"
                + " - Classique : " + arrondir(prixAbonnementClassique, 2) + " €\n"
                + " - HP / HC : " + arrondir(prixAbonnementHPHC, 2) + " €";
    }

    /**
     * Arrondit une valeur à un nombre donné de décimales.
     *
     * @param valeur valeur à arrondir
     * @param decimales nombre de décimales
     * @return valeur arrondie
     */
    private double arrondir(double valeur, int decimales) {
        double facteur = Math.pow(10, decimales);
        return Math.round(valeur * facteur) / facteur;
    }
}

