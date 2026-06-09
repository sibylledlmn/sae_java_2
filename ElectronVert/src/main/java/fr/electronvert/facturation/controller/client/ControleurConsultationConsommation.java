package fr.electronvert.facturation.controller.client;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.GestionnaireContrats;
import fr.electronvert.facturation.view.client.VueConsultationConsommation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Contrôleur de consultation de la consommation électrique du client.
 * <p>
 * Ce contrôleur permet au client de :
 * <ul>
 *   <li>Sélectionner un de ses contrats</li>
 *   <li>Consulter l'historique de consommation mois par mois</li>
 * </ul>
 * La consommation est calculée entre chaque paire de relevés successifs
 * et affichée selon le type d'offre :
 * <ul>
 *   <li>Offre Classique : consommation totale en kWh</li>
 *   <li>Offre HP/HC : consommation HP, HC et total</li>
 * </ul>
 * Au moins 2 relevés sont nécessaires pour afficher la consommation.
 *
 *
 * @see VueConsultationConsommation
 * @see GestionnaireContrats
 * @see Releve
 */
public class ControleurConsultationConsommation {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de consultation de la consommation.
     */
    private final VueConsultationConsommation vue;

    /**
     * Gestionnaire des contrats pour accéder aux relevés.
     */
    private final GestionnaireContrats gestionnaireContrats;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de consultation de la consommation.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireContrats gestionnaire des contrats
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurConsultationConsommation(
            Scanner scanner,
            GestionnaireContrats gestionnaireContrats
    ) {
        if (scanner == null || gestionnaireContrats == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueConsultationConsommation(scanner);
        this.gestionnaireContrats = gestionnaireContrats;
    }

    // =====================
    // DÉMARRAGE
    // =====================

    /**
     * Lance la consultation de la consommation pour un client donné.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter mon historique de consommation</li>
     *   <li>0 : Retour au menu principal</li>
     * </ul>
     * La boucle continue jusqu'à ce que le client choisisse de revenir
     * au menu principal
     *
     * @param client client dont consulter la consommation
     *
     * @throws IllegalArgumentException si le client est null
     */
    public void demarrer(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuConsommation();

            switch (choix) {
                case 1 -> consulterConsommationParContrat(client);
                case 0 -> retour = true;
            }
        }
    }

    // =====================
    // CONSULTATION DE LA CONSOMMATION
    // =====================

    /**
     * Permet de consulter la consommation d'un contrat sélectionné.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Récupération de tous les contrats du client</li>
     *   <li>Affichage de la liste et sélection d'un contrat</li>
     *   <li>Calcul de la consommation entre chaque paire de relevés</li>
     *   <li>Affichage de l'historique chronologique de la consommation</li>
     * </ul>
     * </p>
     * <p>
     * Le formatage dépend de l'offre tarifaire :
     * <ul>
     *   <li><strong>Offre Classique</strong> : Consommation totale uniquement</li>
     *   <li><strong>Offre HP/HC</strong> : HP, HC et total séparément</li>
     * </ul>
     * </p>
     * <p>
     * Si le client n'a aucun contrat ou si le contrat sélectionné a moins
     * de 2 relevés, affiche un message approprié.
     * </p>
     *
     * @param client client dont afficher la consommation
     */
    private void consulterConsommationParContrat(Client client) {
        // Récupération des contrats du client
        List<Contrat> contratsClient = gestionnaireContrats.getContrats().stream()
                .filter(c -> c.getClient().equals(client))
                .toList();

        if (contratsClient.isEmpty()) {
            vue.afficherMessage("Vous n'avez aucun contrat.");
            vue.attendreEntree();
            return;
        }

        // Affichage de la liste des contrats
        List<String> libellesContrats = contratsClient.stream()
                .map(c -> "Contrat " + c.getReference() + " - " + c.getAdressePostale())
                .toList();

        vue.afficherListeContrats(libellesContrats);

        int choixContrat = vue.demanderSelectionContrat(contratsClient.size());

        if (choixContrat == 0) {
            return;
        }

        Contrat contrat = contratsClient.get(choixContrat - 1);

        // Calcul et affichage de la consommation pour le contrat sélectionné
        afficherConsommationContrat(contrat);
    }

    /**
     * Calcule et affiche l'historique de consommation d'un contrat.
     * <p>
     * Les relevés sont triés chronologiquement avant le calcul.
     * Au moins 2 relevés sont nécessaires pour afficher la consommation.
     * </p>
     *
     * @param contrat contrat dont afficher la consommation
     */
    private void afficherConsommationContrat(Contrat contrat) {
        // Tri des relevés par ordre chronologique
        List<Releve> releves = new ArrayList<>(contrat.getReleves());
        releves.sort(null);

        if (releves.size() < 2) {
            vue.afficherHistoriqueConsommation(
                    List.of("Pas assez de relevés pour calculer la consommation.")
            );
            return;
        }

        List<String> lignesConsommationMensuelle = new ArrayList<>();

        // Calcul de la consommation entre chaque paire de relevés
        for (int i = 1; i < releves.size(); i++) {
            Releve precedent = releves.get(i - 1);
            Releve courant = releves.get(i);

            Map<TypeConso, Double> consommation =
                    courant.calculerConsommation(precedent);

            // Formatage de la ligne de consommation
            // Formatage de la ligne de consommation via le modèle
            String ligne = courant.affichageConsommationEntreDeuxReleves(precedent, consommation);

            lignesConsommationMensuelle.add(ligne);
        }

        vue.afficherHistoriqueConsommation(lignesConsommationMensuelle);
    }


}