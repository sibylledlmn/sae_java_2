package fr.electronvert.facturation.controller.client;

import fr.electronvert.facturation.exception.ChangementModeFacturationImpossibleException;
import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.GestionnaireContrats;
import fr.electronvert.facturation.service.SimulateurDate;
import fr.electronvert.facturation.view.client.VueGestionContratsClient;

import java.util.List;
import java.util.Scanner;

/**
 * Contrôleur de gestion des contrats côté client.
 * <p>
 * Ce contrôleur permet au client de :
 * <ul>
 *   <li>Consulter la liste de ses contrats</li>
 *   <li>Sélectionner un contrat pour effectuer des actions</li>
 *   <li>Consulter les informations détaillées d'un contrat</li>
 *   <li>Demander un changement d'offre tarifaire (Classique ↔ HP/HC)</li>
 *   <li>Demander un changement de mode de facturation (REEL ↔ ECHEANCIER)</li>
 * </ul>
 * Les demandes de changement sont enregistrées et appliquées automatiquement
 * par le {@link SimulateurDate} aux dates prévues (1er du mois pour l'offre tarifaire,
 * 6 du mois pour le mode de facturation).
 *
 * @see VueGestionContratsClient
 * @see GestionnaireContrats
 * @see SimulateurDate
 */
public class ControleurGestionContratsClient {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de gestion des contrats client.
     */
    private final VueGestionContratsClient vue;

    /**
     * Gestionnaire des contrats pour les opérations métier.
     */
    private final GestionnaireContrats gestionnaireContrats;

    /**
     * Simulateur de date pour obtenir la date courante lors des demandes.
     */
    private final SimulateurDate simulateurDate;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de gestion des contrats client.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireContrats gestionnaire des contrats
     * @param simulateurDate simulateur de date
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurGestionContratsClient(
            Scanner scanner,
            GestionnaireContrats gestionnaireContrats,
            SimulateurDate simulateurDate
    ) {
        if (scanner == null || gestionnaireContrats == null || simulateurDate == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueGestionContratsClient(scanner);
        this.gestionnaireContrats = gestionnaireContrats;
        this.simulateurDate = simulateurDate;
    }

    // =====================
    // DÉMARRAGE
    // =====================

    /**
     * Lance la gestion des contrats pour un client donné.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter mes contrats</li>
     *   <li>0 : Retour au menu principal</li>
     * </ul>
     * La boucle continue jusqu'à ce que le client choisisse de revenir
     * au menu principal.
     *
     * @param client client dont gérer les contrats
     *
     * @throws IllegalArgumentException si le client est null
     */
    public void demarrer(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuContrats();

            switch (choix) {
                case 1 -> consulterContrats(client);
                case 0 -> retour = true;
            }
        }
    }

    // =====================
    // CONSULTATION DES CONTRATS
    // =====================

    /**
     * Affiche la liste des contrats du client et permet d'en sélectionner un.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Récupération de tous les contrats du client</li>
     *   <li>Affichage de la liste avec résumé de chaque contrat</li>
     *   <li>Sélection d'un contrat (ou 0 pour retour)</li>
     *   <li>Affichage du menu contextuel pour le contrat sélectionné</li>
     * </ul>
     * Si le client n'a aucun contrat, affiche un message approprié.
     * </p>
     *
     * @param client client dont consulter les contrats
     */
    private void consulterContrats(Client client) {
        // Récupération des contrats du client
        List<Contrat> contratsClient = gestionnaireContrats.getContratsClient(client);

        if (contratsClient.isEmpty()) {
            vue.afficherListeContrats(List.of());
            return;
        }

        boolean retour = false;

        while (!retour) {
            // Affichage de la liste
            List<String> libelles = contratsClient.stream()
                    .map(Contrat::toResume)
                    .toList();

            vue.afficherListeContrats(libelles);

            int selection = vue.demanderSelectionContrat(contratsClient.size());

            if (selection == 0) {
                return;
            }

            Contrat contrat = contratsClient.get(selection - 1);

            retour = gererContratSelectionne(contrat);
        }
    }

    // =====================
    // MENU CONTEXTUEL CONTRAT
    // =====================

    /**
     * Gère le menu contextuel d'actions sur un contrat sélectionné.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter les informations du contrat</li>
     *   <li>2 : Demander un changement d'offre tarifaire</li>
     *   <li>3 : Demander un changement de mode de facturation</li>
     *   <li>0 : Retour à la liste des contrats</li>
     * </ul>
     * Les erreurs métier (changement impossible, contraintes non respectées)
     * sont capturées et affichées à l'utilisateur.
     * </p>
     *
     * @param contrat contrat sur lequel effectuer des actions
     * @return {@code false} pour rester dans la liste des contrats
     */
    private boolean gererContratSelectionne(Contrat contrat) {
        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuContratSelectionne();

            try {
                switch (choix) {
                    case 1 -> afficherInformationsContrat(contrat);

                    case 2 -> demanderChangementOffre(contrat);

                    case 3 -> demanderChangementMode(contrat);

                    case 0 -> retour = true;
                }
            } catch (Exception e) {
                vue.afficherErreur(e.getMessage());
                vue.attendreEntree();
            }
        }

        return false;
    }

    // =====================
    // ACTIONS SUR CONTRAT
    // =====================

    /**
     * Affiche les informations détaillées du contrat.
     * <p>
     * Les informations incluent : référence, adresse, client, offre tarifaire,
     * mode de facturation, dates, statut, etc.
     * </p>
     *
     * @param contrat contrat dont afficher les informations
     */
    private void afficherInformationsContrat(Contrat contrat) {
        vue.afficherInformationsContrat(contrat.getDetails());
    }

    /**
     * Enregistre une demande de changement d'offre tarifaire.
     * <p>
     * Le changement bascule entre les deux offres disponibles :
     * <ul>
     *   <li>Classique → HP/HC (Heures Pleines / Heures Creuses)</li>
     *   <li>HP/HC → Classique</li>
     * </ul>
     * La demande sera appliquée le 1er du mois suivant par le {@link SimulateurDate}.
     * </p>
     * <p>
     * Des frais de 75€ HT sont facturés si le changement n'est pas demandé le mois
     * précédent la date anniversaire du contrat. L'utilisateur est informé et
     * doit confirmer la demande avant son enregistrement.
     * </p>
     *
     * @param contrat contrat pour lequel demander le changement
     */
    private void demanderChangementOffre(Contrat contrat) {
        // Vérification de la gratuité du changement
        boolean gratuit = contrat.changementOffreGratuit(
                simulateurDate.getDateCourante()
        );

        String messageInfo = "La demande prendra effet le 1er du mois suivant.";

        if (!gratuit) {
            messageInfo += "\n\nATTENTION : Des frais de 75€ HT seront facturés car la demande\n" +
                    "n'est pas effectuée durant le mois anniversaire du contrat.";
        }

        vue.afficherDemande(
                "Changement d'offre tarifaire",
                messageInfo
        );

        // Détermination de la nouvelle offre
        OffreTarifaire nouvelleOffre;
        String nomNouvelleOffre;

        if (contrat.getOffreTarifaire() instanceof OffreClassique) {
            nouvelleOffre = new OffreHPHC();
            nomNouvelleOffre = "Heures Pleines / Heures Creuses";
        } else {
            nouvelleOffre = new OffreClassique();
            nomNouvelleOffre = "Classique";
        }

        // Demande de confirmation
        boolean confirmation = vue.demanderConfirmation(
                "Confirmer le changement vers l'offre " + nomNouvelleOffre + " ?"
        );

        if (!confirmation) {
            vue.afficherMessage("Changement d'offre annulé.");
            vue.attendreEntree();
            return;
        }

        // Enregistrement de la demande
        try {
            gestionnaireContrats.demanderChangementOffreTarifaire(
                    contrat,
                    nouvelleOffre,
                    simulateurDate.getDateCourante()
            );

            vue.afficherMessage("Demande de changement d'offre enregistrée.");
            vue.attendreEntree();

        } catch (Exception e) {
            vue.afficherMessage("Erreur : " + e.getMessage());
            vue.attendreEntree();
        }
    }

    /**
     * Enregistre une demande de changement de mode de facturation.
     * <p>
     * Le changement bascule entre les deux modes disponibles :
     * <ul>
     *   <li>REEL → ECHEANCIER (mensualités fixes + régularisation annuelle)</li>
     *   <li>ECHEANCIER → REEL (facturation mensuelle au réel)</li>
     * </ul>
     * La demande sera appliquée le 6 du mois suivant par le {@link SimulateurDate}.
     * Le changement n'est possible qu'une fois par an, et la demande doit se faire
     * le mois précédent la date anniversaire du contrat.
     * </p>
     * <p>
     * L'utilisateur est informé du nouveau mode et doit confirmer
     * la demande avant son enregistrement.
     * </p>
     *
     * @param contrat contrat pour lequel demander le changement
     *
     * @throws ChangementModeFacturationImpossibleException si le changement
     *         n'est pas autorisé à cette date (déjà effectué dans l'année)
     */
    private void demanderChangementMode(Contrat contrat) {
        // Détermination du nouveau mode (bascule)
        ModeFacturation nouveauMode;
        String nomNouveauMode;

        if (contrat.getModeFacturation() == ModeFacturation.REEL) {
            nouveauMode = ModeFacturation.ECHEANCIER;
            nomNouveauMode = "Échéancier (mensualités fixes)";
        } else {
            nouveauMode = ModeFacturation.REEL;
            nomNouveauMode = "Facturation mensuelle au réel";
        }

        vue.afficherDemande(
                "Changement de mode de facturation",
                "La demande prendra effet le 6 du mois suivant.\n" +
                        "Nouveau mode : " + nomNouveauMode
        );

        // Demande de confirmation
        boolean confirmation = vue.demanderConfirmation(
                "Confirmer le changement vers le mode " + nomNouveauMode + " ?"
        );

        if (!confirmation) {
            vue.afficherMessage("Changement de mode annulé.");
            vue.attendreEntree();
            return;
        }

        try {
            gestionnaireContrats.demanderChangementModeFacturation(
                    contrat,
                    nouveauMode,
                    simulateurDate.getDateCourante()
            );

            vue.afficherMessage("Demande de changement de mode enregistrée.");
            vue.attendreEntree();

        } catch (ChangementModeFacturationImpossibleException e) {
            vue.afficherMessage("Erreur : " + e.getMessage());
            vue.attendreEntree();
        }
    }
}
