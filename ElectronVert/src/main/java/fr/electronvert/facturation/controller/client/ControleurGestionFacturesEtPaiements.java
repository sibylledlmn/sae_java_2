package fr.electronvert.facturation.controller.client;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.Paiement;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.GestionnaireContrats;
import fr.electronvert.facturation.service.GestionnairePaiements;
import fr.electronvert.facturation.service.SimulateurDate;
import fr.electronvert.facturation.view.client.VueGestionFacturesEtPaiements;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Contrôleur de gestion des factures et paiements côté client.
 * <p>
 * Ce contrôleur permet au client de :
 * <ul>
 *   <li>Sélectionner un de ses contrats</li>
 *   <li>Consulter toutes les factures du contrat</li>
 *   <li>Consulter uniquement les factures impayées</li>
 *   <li>Consulter uniquement les factures payées</li>
 *   <li>Payer une facture impayée</li>
 *   <li>Consulter l'historique des paiements</li>
 * </ul>
 * Le workflow standard est : sélection du contrat → menu d'actions sur
 * les factures et paiements de ce contrat.
 *
 *
 * @see VueGestionFacturesEtPaiements
 * @see GestionnaireContrats
 * @see GestionnairePaiements
 * @see SimulateurDate
 */
public class ControleurGestionFacturesEtPaiements {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de gestion des factures et paiements.
     */
    private final VueGestionFacturesEtPaiements vue;

    /**
     * Gestionnaire des contrats pour la sélection.
     */
    private final GestionnaireContrats gestionnaireContrats;

    /**
     * Gestionnaire des paiements pour l'enregistrement des paiements.
     */
    private final GestionnairePaiements gestionnairePaiements;

    /**
     * Simulateur de date pour obtenir la date courante lors des paiements.
     */
    private final SimulateurDate simulateurDate;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de gestion des factures et paiements.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireContrats gestionnaire des contrats
     * @param gestionnairePaiements gestionnaire des paiements
     * @param simulateurDate simulateur de date
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurGestionFacturesEtPaiements(
            Scanner scanner,
            GestionnaireContrats gestionnaireContrats,
            GestionnairePaiements gestionnairePaiements,
            SimulateurDate simulateurDate
    ) {
        if (scanner == null || gestionnaireContrats == null
                 || gestionnairePaiements == null
                || simulateurDate == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueGestionFacturesEtPaiements(scanner);
        this.gestionnaireContrats = gestionnaireContrats;
        this.gestionnairePaiements = gestionnairePaiements;
        this.simulateurDate = simulateurDate;
    }

    // =====================
    // DÉMARRAGE
    // =====================

    /**
     * Point d'entrée de la gestion des factures et paiements pour un client.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Sélection d'un contrat parmi ceux du client</li>
     *   <li>Affichage du menu d'actions sur les factures et paiements</li>
     *   <li>Retour à la sélection de contrat après chaque action</li>
     * </ul>
     * La boucle continue jusqu'à ce que le client choisisse de revenir
     * au menu principal (choix 0 lors de la sélection du contrat).
     *
     * @param client client dont gérer les factures et paiements
     *
     * @throws IllegalArgumentException si le client est null
     */
    public void demarrer(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        while (true) {
            Contrat contrat = selectionnerContrat(client);
            if (contrat == null) {
                return;
            }

            gererFacturesEtPaiementsPourContrat(contrat);
        }
    }

    // =====================
    // SÉLECTION DU CONTRAT
    // =====================

    /**
     * Permet au client de sélectionner un de ses contrats.
     * <p>
     * Affiche la liste des contrats du client (référence + adresse) et
     * demande une sélection. Si le client n'a aucun contrat, affiche
     * un message approprié.
     * </p>
     *
     * @param client client dont afficher les contrats
     * @return contrat sélectionné ou {@code null} si retour au menu principal
     */
    private Contrat selectionnerContrat(Client client) {
        // Récupération des contrats du client
        List<Contrat> contratsClient = gestionnaireContrats.getContratsClient(client);

        // Cas : aucun contrat
        if (contratsClient.isEmpty()) {
            vue.afficherListeContrats(List.of());
            return null;
        }

        // Préparation des libellés pour affichage
        List<String> libelles = contratsClient.stream()
                .map(Contrat::toResume)
                .toList();

        vue.afficherListeContrats(libelles);

        int choix = vue.demanderSelectionContrat(contratsClient.size());

        if (choix == 0) {
            return null;
        }

        return contratsClient.get(choix - 1);
    }

    // =====================
    // MENU FACTURES & PAIEMENTS
    // =====================

    /**
     * Gère le menu d'actions sur les factures et paiements d'un contrat.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter toutes les factures</li>
     *   <li>2 : Consulter les factures impayées</li>
     *   <li>3 : Consulter les factures payées</li>
     *   <li>4 : Payer une facture</li>
     *   <li>5 : Consulter l'historique des paiements</li>
     *   <li>0 : Retour (retour à la sélection de contrat)</li>
     * </ul>
     * La boucle continue jusqu'à ce que le client choisisse de revenir
     * à la sélection de contrat.
     * </p>
     *
     * @param contrat contrat dont gérer les factures et paiements
     */
    private void gererFacturesEtPaiementsPourContrat(Contrat contrat) {
        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuFacturesEtPaiements();

            switch (choix) {
                case 1 -> consulterFactures(
                        "Toutes les factures",
                        contrat.getFactures()
                );

                case 2 -> consulterFactures(
                        "Factures impayées",
                        contrat.getFacturesImpayees()
                );

                case 3 -> consulterFactures(
                        "Factures payées",
                        contrat.getFacturesPayees()
                );

                case 4 -> payerFacture(contrat);

                case 5 -> consulterHistoriquePaiements(contrat);

                case 0 -> retour = true;
            }
        }
    }

    // =====================
    // CONSULTATION DES FACTURES
    // =====================

    /**
     * Affiche une liste de factures et permet d'en consulter les détails.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Affichage de la liste des factures avec leur résumé</li>
     *   <li>Sélection d'une facture (ou 0 pour retour)</li>
     *   <li>Affichage des détails complets de la facture sélectionnée</li>
     * </ul>
     * Si aucune facture n'est disponible, affiche un message approprié.
     * </p>
     *
     * @param titre titre à afficher (ex: "Toutes les factures", "Factures impayées")
     * @param factures liste des factures à afficher
     */
    private void consulterFactures(String titre, List<Facture> factures) {
        List<String> resumes = factures.stream()
                .map(Facture::toResume)
                .toList();

        vue.afficherListeFactures(titre, resumes);

        if (factures.isEmpty()) {
            return;
        }

        int choix = vue.demanderChoixFacture(factures.size());

        if (choix == 0) {
            return;
        }

        Facture facture = factures.get(choix - 1);
        vue.afficherDetailsFacture(facture.getDetails());
    }

    // =====================
    // PAIEMENT DE FACTURES
    // =====================

    /**
     * Permet au client de payer une facture impayée.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Affichage de la liste des factures impayées du contrat</li>
     *   <li>Sélection d'une facture à payer (ou 0 pour retour)</li>
     *   <li>Enregistrement du paiement à la date courante</li>
     *   <li>Affichage de la confirmation du paiement</li>
     * </ul>
     * Si aucune facture impayée n'existe, affiche un message approprié.
     * </p>
     *
     * @param contrat contrat dont payer une facture
     */
    private void payerFacture(Contrat contrat) {
        // Récupération des factures à payer (statue émises et impayées)
        List<Facture> facturesAPayer = contrat.getFacturesApayer();

        if (facturesAPayer.isEmpty()) {
            vue.afficherFacturesAPayer(List.of());
            return;
        }

        // Affichage et sélection des factures à payer
        List<String> resumes = facturesAPayer.stream()
                .map(Facture::toResume)
                .toList();

        vue.afficherFacturesAPayer(resumes);

        int choix = vue.demanderChoixFactureAPayer(facturesAPayer.size());

        if (choix == 0) {
            return;
        }
        Facture facture = facturesAPayer.get(choix - 1);

        // Demande de confirmation avec le montant
        String messageConfirmation = String.format(
                "Confirmer le paiement de %.2f € pour la facture %s ?",
                facture.getMontantTotalTTCAPayer(),
                facture.getReference()
        );

        boolean confirmation = vue.demanderConfirmation(messageConfirmation);

        if (!confirmation) {
            vue.afficherMessage("Paiement annulé.");
            vue.attendreEntree();
            return;
        }

        // Paiement de la facture
        try {
            Paiement paiement = gestionnairePaiements.payerFacture(
                    facture,
                    simulateurDate.getDateCourante()
            );

            if (paiement == null) {
                vue.afficherMessage(
                        "La facture a été réglée automatiquement grâce à votre solde créditeur."
                );
                vue.attendreEntree();
            } else {
                vue.afficherPaiementReussi(paiement.getDetails());
            }

        } catch (Exception e) {
            vue.afficherMessage("Erreur lors du paiement : " + e.getMessage());
            vue.attendreEntree();
        }

    }

    // =====================
    // HISTORIQUE DES PAIEMENTS
    // =====================

    /**
     * Affiche l'historique des paiements d'un contrat et permet d'en
     * consulter les détails.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Affichage de la liste de tous les paiements du contrat</li>
     *   <li>Sélection d'un paiement (ou 0 pour retour)</li>
     *   <li>Affichage des détails complets du paiement sélectionné</li>
     * </ul>
     * Si aucun paiement n'existe, affiche un message approprié.
     * Les paiements incluent les factures payées et les mensualités prélevées.
     * </p>
     *
     * @param contrat contrat dont consulter l'historique des paiements
     */
    private void consulterHistoriquePaiements(Contrat contrat) {

        // Récupération des paiements de toutes les factures du contrat (peut contenir des null)
        List<Paiement> paiements =
                gestionnairePaiements.getPaiementsPourContrat(contrat)
                        .stream()
                        .filter(Objects::nonNull) // on ignore les paiements inexistants
                        .toList();

        // Aucun paiement à afficher
        if (paiements.isEmpty()) {
            vue.afficherMessage(
                    "Aucun paiement enregistré.\n");
            vue.attendreEntree();
            return;
        }

        // Résumés des paiements
        List<String> resumes = paiements.stream()
                .map(Paiement::toString)
                .toList();

        vue.afficherListePaiements(resumes);

        int choix = vue.demanderChoixPaiement(paiements.size());

        if (choix == 0) {
            return;
        }

        Paiement paiement = paiements.get(choix - 1);

        vue.afficherDetailsPaiement(paiement.getDetails());
    }


}