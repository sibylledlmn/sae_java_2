package fr.electronvert.facturation.controller.client;

import fr.electronvert.facturation.service.GestionnaireTarifs;
import fr.electronvert.facturation.service.SimulateurDate;
import fr.electronvert.facturation.view.client.VueConsultationTarifs;

import java.util.List;
import java.util.Scanner;

/**
 * Contrôleur de consultation des tarifs ElectronVert côté client.
 * <p>
 * Ce contrôleur permet au client de consulter :
 * <ul>
 *   <li>Le tarif actuellement en vigueur</li>
 *   <li>L'historique complet des tarifs (du plus ancien au plus récent)</li>
 * </ul>
 * La consultation est en lecture seule : le client peut uniquement visualiser
 * les informations tarifaires (prix du kWh, prix des abonnements) sans pouvoir
 * les modifier.
 *
 * @see VueConsultationTarifs
 * @see GestionnaireTarifs
 * @see SimulateurDate
 */
public class ControleurConsultationTarifs {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de consultation des tarifs.
     */
    private final VueConsultationTarifs vueConsultationTarifs;

    /**
     * Gestionnaire des tarifs pour récupérer les données.
     */
    private final GestionnaireTarifs gestionnaireTarifs;

    /**
     * Simulateur de date pour obtenir la date courante et déterminer le tarif actif.
     */
    private final SimulateurDate simulateurDate;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de consultation des tarifs.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireTarifs gestionnaire des tarifs
     * @param simulateurDate simulateur de date
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurConsultationTarifs(
            Scanner scanner,
            GestionnaireTarifs gestionnaireTarifs,
            SimulateurDate simulateurDate
    ) {
        if (scanner == null || gestionnaireTarifs == null || simulateurDate == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vueConsultationTarifs = new VueConsultationTarifs(scanner);
        this.gestionnaireTarifs = gestionnaireTarifs;
        this.simulateurDate = simulateurDate;
    }

    // =====================
    // CONSULTATION DES TARIFS
    // =====================

    /**
     * Lance la consultation des tarifs (tarif actif + historique).
     * <p>
     * Affiche successivement :
     * <ul>
     *   <li>Le tarif actuellement en vigueur (le plus récent applicable à la date du jour)</li>
     *   <li>L'historique complet de tous les tarifs, triés par date croissante</li>
     * </ul>
     * Chaque tarif présenté comprend sa date de début, les prix du kWh
     * pour les offres Classique et HP/HC, et les prix des abonnements annuels.
     *
     * <p>
     * Cette méthode ne nécessite pas de paramètre client car les tarifs
     * sont identiques pour tous les clients.
     * </p>
     */
    public void demarrer() {
        // Tarif en vigueur
        String tarifActuel = gestionnaireTarifs
                .getTarifActif(simulateurDate.getDateCourante())
                .toString();

        vueConsultationTarifs.afficherTarifActuel(tarifActuel);

        // Historique des tarifs
        List<String> historiqueTarifs = gestionnaireTarifs
                .getHistoriqueDesTarifs()
                .stream()
                .map(Object::toString)
                .toList();

        vueConsultationTarifs.afficherHistoriqueTarifs(historiqueTarifs);
    }
}