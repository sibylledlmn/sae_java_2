package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.DateChangementTarifInvalideException;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service de gestion de l'historique des tarifs d'électricité.
 * <p>
 * Cette classe est responsable de :
 * <ul>
 *   <li>La création de nouveaux tarifs avec validation des dates</li>
 *   <li>Le maintien d'un historique ordonné des tarifs</li>
 *   <li>La fourniture du tarif en vigueur à une date donnée</li>
 * </ul>
 * Les changements de tarifs sont autorisés uniquement le 1er février
 * et le 1er août de chaque année.
 *
 *
 * @see Tarif
 */
public class GestionnaireTarifs {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Historique de tous les tarifs, triés par date de début croissante.
     */
    private final List<Tarif> historiqueDesTarifs = new ArrayList<>();

    // =====================
    // CRÉATION DE TARIFS
    // =====================

    /**
     * Crée et enregistre un nouveau tarif.
     * <p>
     * Les conditions suivantes doivent être respectées :
     * <ul>
     *   <li>La date de début doit être un 1er février ou 1er août</li>
     *   <li>La date de début doit être postérieure à celle du dernier tarif enregistré</li>
     * </ul>
     * Le tarif est automatiquement inséré dans l'historique de manière triée.
     *
     *
     * @param dateDebut date de début du nouveau tarif (1er février ou 1er août)
     * @param prixKwhClassique prix du kWh pour l'offre Classique (en €)
     * @param prixKwhHeuresPleines prix du kWh en heures pleines pour l'offre HP/HC (en €)
     * @param prixKwhHeuresCreuses prix du kWh en heures creuses pour l'offre HP/HC (en €)
     * @param prixAbonnementClassique prix de l'abonnement annuel Classique (en €)
     * @param prixAbonnementHPHC prix de l'abonnement annuel HP/HC (en €)
     * @return tarif créé et ajouté à l'historique
     *
     * @throws DateChangementTarifInvalideException si la date n'est pas un 1er février ou 1er août
     * @throws IllegalArgumentException si la date n'est pas postérieure au dernier tarif
     */
    public Tarif creerNouveauTarif(
            LocalDate dateDebut,
            double prixKwhClassique,
            double prixKwhHeuresPleines,
            double prixKwhHeuresCreuses,
            double prixAbonnementClassique,
            double prixAbonnementHPHC
    ) {
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début ne peut pas être null");
        }

        // Validation de la date de changement de tarif
        if (!estDateValideChangementTarif(dateDebut)) {
            throw new DateChangementTarifInvalideException(dateDebut);
        }

        // Vérification que la date est postérieure au dernier tarif
        if (!historiqueDesTarifs.isEmpty()) {
            Tarif dernierTarif = historiqueDesTarifs.get(historiqueDesTarifs.size() - 1);

            if (!dateDebut.isAfter(dernierTarif.getDateDebut())) {
                throw new IllegalArgumentException(
                        "La date de début du nouveau tarif doit être postérieure au tarif actuel"
                );
            }
        }

        // Création du nouveau tarif
        Tarif nouveauTarif = new Tarif(
                dateDebut,
                prixKwhClassique,
                prixKwhHeuresPleines,
                prixKwhHeuresCreuses,
                prixAbonnementClassique,
                prixAbonnementHPHC
        );

        // Ajout à l'historique
        historiqueDesTarifs.add(nouveauTarif);
        Collections.sort(historiqueDesTarifs);

        return nouveauTarif;
    }

    /**
     * Ajoute un tarif initial au système.
     * <p>
     * Cette méthode ne peut être appelée qu'une seule fois pour initialiser
     * le premier tarif du système. Elle est utilisée lors du démarrage
     * de l'application pour définir le tarif de base.
     * Il n'y a pas de contrainte de date pour le tarif initial
     * </p>
     *
     * @param tarif tarif initial à ajouter
     *
     * @throws IllegalArgumentException si le tarif est null
     * @throws IllegalStateException si un tarif existe déjà
     */
    public void ajouterTarifInitial(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Le tarif ne peut pas être nul");
        }

        if (!historiqueDesTarifs.isEmpty()) {
            throw new IllegalStateException(
                    "Le tarif initial ne peut être ajouté que si aucun tarif n'existe"
            );
        }

        historiqueDesTarifs.add(tarif);
    }

    // =====================
    // CONSULTATION DES TARIFS
    // =====================

    /**
     * Retourne le tarif actif à une date donnée.
     * <p>
     * Le tarif actif est le plus récent dont la date de début est
     * antérieure ou égale à la date fournie. Par exemple, si l'historique
     * contient des tarifs du 01/02/2024 et 01/08/2024, une requête pour
     * le 15/06/2024 retournera le tarif du 01/02/2024.
     * </p>
     *
     * @param date date pour laquelle obtenir le tarif
     * @return tarif en vigueur à cette date
     *
     * @throws IllegalArgumentException si la date est null
     * @throws IllegalStateException si aucun tarif n'existe pour cette date
     */
    public Tarif getTarifActif(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }

        return historiqueDesTarifs.stream()
                .filter(t -> !t.getDateDebut().isAfter(date))
                .max(Tarif::compareTo)
                .orElseThrow(() ->
                        new IllegalStateException("Aucun tarif actif pour la date : " + date)
                );
    }

    /**
     * Retourne l'historique complet des tarifs.
     *
     * @return liste non modifiable de tous les tarifs, triée par date croissante
     */
    public List<Tarif> getHistoriqueDesTarifs() {
        return Collections.unmodifiableList(historiqueDesTarifs);
    }

    // =====================
    // VALIDATION
    // =====================

    /**
     * Vérifie si une date est valide pour un changement de tarif.
     * <p>
     * Les changements de tarifs sont autorisés uniquement le :
     * <ul>
     *   <li>1er février</li>
     *   <li>1er août</li>
     * </ul>
     * Cette restriction correspond aux dates réglementaires de révision
     * des tarifs de l'électricité.
     * </p>
     *
     * @param date date à vérifier
     * @return {@code true} si la date est un 1er février ou 1er août,
     *         {@code false} sinon
     */
    private boolean estDateValideChangementTarif(LocalDate date) {
        return (date.getMonthValue() == 2 && date.getDayOfMonth() == 1)
                || (date.getMonthValue() == 8 && date.getDayOfMonth() == 1);
    }
}