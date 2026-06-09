package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.exception.ChangementModeFacturationImpossibleException;
import fr.electronvert.facturation.exception.ChangementOffreImpossibleException;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Représente un contrat d'électricité souscrit par un client.
 * <p>
 * Un contrat est définit par:
 * <ul>
 *     <li>une offre tarifaire</li>
 *     <li>un mode de facturation</li>
 *     <li>un historique de factures et de relevés</li>
 *     <li>un état (actif ou clôturé)</li>
 * </ul>
 * Il constitue l'entité centrale de la facturation.
 *
 */
public class Contrat {

    // =====================
    // IDENTITÉ
    // =====================

    private final UUID id;
    private static int compteurContrats = 1;
    private final String reference;

    // =====================
    // LIENS & DONNÉES GÉNÉRALES
    // =====================

    private final Utilisateur client;
    private final String adressePostale;

    private OffreTarifaire offreTarifaire;
    private OffreTarifaire offreTarifaireFuture;

    private ModeFacturation modeFacturation;
    private ModeFacturation modeFacturationFutur;

    private StatutContrat statut;

    private final LocalDate dateSouscription;
    private LocalDate dateFin;

    private String numeroCompteur; // informatif

    // =====================
    // FACTURATION
    // =====================

    private Echeancier echeancier;
    private final List<Echeancier> echeanciers = new ArrayList<>();
    private final List<Facture> factures = new ArrayList<>();
    private final List<Releve> releves = new ArrayList<>();

    private boolean facturationTerminee = false;

    // =====================
    // FINANCIER
    // =====================

    private double fraisChangementOffreEnAttente;
    private double soldeCrediteur;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un contrat actif à la date de souscription.
     *
     * @param client client titulaire du contrat
     * @param adressePostale adresse du logement
     * @param offreTarifaire offre tarifaire choisie
     * @param modeFacturation mode de facturation
     * @param dateSouscription date de souscription du contrat
     */
    public Contrat(Utilisateur client,
                   String adressePostale,
                   OffreTarifaire offreTarifaire,
                   ModeFacturation modeFacturation,
                   LocalDate dateSouscription) {

        if (client.getRole() != RoleUtilisateur.CLIENT) {
            throw new IllegalArgumentException("Un contrat doit être associé à un client");
        }
        this.id = UUID.randomUUID();
        this.reference = genererReference(dateSouscription);
        this.client = client;
        this.adressePostale = adressePostale;
        this.offreTarifaire = offreTarifaire;
        this.modeFacturation = modeFacturation;
        this.dateSouscription = dateSouscription;
        this.statut = StatutContrat.ACTIF;
        this.fraisChangementOffreEnAttente = 0.0;
    }

    /**
     * Génère une référence unique de contrat.
     */
    private String genererReference(LocalDate date) {
        return String.format(
                "CTR-%d-%06d",
                date.getYear(),
                compteurContrats++
        );
    }

    // =====================
    // GETTERS SIMPLES
    // =====================

    public UUID getId() { return id; }
    public String getReference() { return reference; }
    public Utilisateur getClient() { return client; }
    public String getAdressePostale() { return adressePostale; }
    public OffreTarifaire getOffreTarifaire() { return offreTarifaire; }
    public OffreTarifaire getOffreTarifaireFuture() { return offreTarifaireFuture; }
    public ModeFacturation getModeFacturation() { return modeFacturation; }
    public ModeFacturation getModeFacturationFutur() { return modeFacturationFutur; }
    public StatutContrat getStatut() { return statut; }
    public LocalDate getDateSouscription() { return dateSouscription; }
    public LocalDate getDateFin() { return dateFin; }
    public double getFraisChangementOffreEnAttente() { return fraisChangementOffreEnAttente; }
    public double getSoldeCrediteur() { return soldeCrediteur; }
    public boolean estFacturationTerminee() { return facturationTerminee; }
    public Echeancier getEcheancier() { return echeancier; }
    public List<Facture> getFactures() {return Collections.unmodifiableList(factures);}
    public List<Releve> getReleves() {return Collections.unmodifiableList(releves);}
    public List<Echeancier> getEcheanciers() {return Collections.unmodifiableList(echeanciers);}

    // =====================
    // ÉTAT DU CONTRAT
    // =====================

    /**
     * Indique si le contrat est actuellement actif.
     *
     * @return {@code true} si le contrat est actif, {@code false} s'il est clôturé
     */
    public boolean estActif() {
        return statut == StatutContrat.ACTIF;
    }

    /**
     * Clôture le contrat à la date indiquée.
     * <p>
     * Un contrat clôturé n'est plus considéré comme actif et ne peut plus
     * faire l'objet de nouvelles opérations de facturation après sa dernière facture.
     * </p>
     *
     * @param dateFin date de fin du contrat
     */
    public void cloturer(LocalDate dateFin) {
        this.statut = StatutContrat.CLOTURE;
        this.dateFin = dateFin;
    }

    // =====================
    // FACTURES & RELEVÉS
    // =====================

    /**
     * Ajoute une facture à l'historique du contrat.
     *
     * @param facture facture à rattacher au contrat
     */
    public void ajouterFacture(Facture facture) {
        factures.add(facture);
    }

    /**
     * Ajoute un relevé de consommation au contrat.
     *
     * @param releve relevé à rattacher au contrat
     */
    public void ajouterReleve(Releve releve) {
        releves.add(releve);
    }




    /**
     * Retourne la liste des factures impayées du contrat.
     *
     * @return liste des factures dont le statut est {@code IMPAYEE}
     */
    public List<Facture> getFacturesImpayees() {
        return factures.stream()
                .filter(f -> f.getStatut() == StatutFacture.IMPAYEE)
                .toList();
    }

    /**
     * Retourne la liste des factures à payer du contrat
     *
     * @return liste des factures dont le statut est {@code IMPAYEE} ou {@code EMISE}
     */
    public List<Facture> getFacturesApayer() {
        return factures.stream()
                .filter(f -> f.getStatut() == StatutFacture.IMPAYEE ||
                        f.getStatut() == StatutFacture.EMISE)
                .toList();
    }

    /**
     * Retourne la liste des factures payées du contrat.
     *
     * @return liste des factures dont le statut est {@code PAYEE}
     */
    public List<Facture> getFacturesPayees() {
        return factures.stream()
                .filter(f -> f.getStatut() == StatutFacture.PAYEE)
                .toList();
    }

    /**
     * Indique si le contrat possède au moins une facture impayée.
     *
     * @return {@code true} si une facture est impayée
     */
    public boolean aDesFacturesImpayees() {
        return factures.stream()
                .anyMatch(f -> f.getStatut() == StatutFacture.IMPAYEE);
    }


    /**
     * Retourne le dernier relevé de consommation du contrat.
     *
     * @return dernier relevé ou {@code null} s'il n'existe pas
     */
    public Releve getDernierReleve() {
        return releves.isEmpty() ? null : Collections.max(releves);
    }

    /**
     * Retourne l'avant-dernier relevé de consommation du contrat.
     *
     * @return avant-dernier relevé ou {@code null} s'il n'existe pas
     */
    public Releve getAvantDernierReleve() {
        if (releves.size() < 2) return null;
        List<Releve> copie = new ArrayList<>(releves);
        Collections.sort(copie);
        return copie.get(copie.size() - 2);
    }

    /**
     * Marque la facturation du contrat comme terminée.
     * <p>
     * Utilisée notamment après l'émission de la facture de clôture
     * afin d'empêcher toute facturation ultérieure.
     * </p>
     */
    public void marquerFacturationTerminee() {
        this.facturationTerminee = true;
    }

    public Releve trouverReleveADate(LocalDate date) {

        return getReleves().stream()
                .filter(r -> r.getDateDeReleve().equals(date))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Aucun relevé trouvé à la date " + date
                        )
                );
    }



    // =====================
    // ÉCHÉANCIERS
    // =====================


    /**
     * Attache un nouvel échéancier au contrat.
     * <p>
     * Si un échéancier était déjà actif, celui-ci est automatiquement terminé.
     * </p>
     *
     * @param nouvelEcheancier échéancier à activer
     */
    public void attacherNouvelEcheancier(Echeancier nouvelEcheancier) {
        if (this.echeancier != null) {
            this.echeancier.terminer();
        }
        this.echeancier = nouvelEcheancier;
    }

    /**
     * Supprime l'échéancier actif du contrat.
     * <p>
     * L'échéancier est marqué comme terminé avant suppression.
     * </p>
     */
    public void supprimerEcheancier() {
        if (this.echeancier != null) {
            this.echeancier.terminer();
            this.echeancier = null;
        }
    }

    /**
     * Ajoute un échéancier à l'historique du contrat.
     *
     * @param echeancier échéancier à ajouter
     * @throws IllegalArgumentException si l'échéancier est nul
     */
    public void ajouterEcheancier(Echeancier echeancier) {
        if (echeancier == null) {
            throw new IllegalArgumentException("L'échéancier ne peut pas être null");
        }
        echeanciers.add(echeancier);
    }



    // =====================
    // CHANGEMENTS OFFRE TARIFAIRE/ MODE DE FACTURATION
    // =====================

    /**
     * Planifie un changement d'offre tarifaire pour le contrat.
     * <p>
     * Le changement n'est pas appliqué immédiatement mais stocké
     * comme une offre future, qui sera appliquée ultérieurement
     * par le système de facturation.
     * </p>
     *
     * @param nouvelleOffre nouvelle offre tarifaire souhaitée
     * @throws ChangementOffreImpossibleException si l'offre demandée
     *         est identique à l'offre actuelle
     */
    public void planifierChangementOffreTarifaire(OffreTarifaire nouvelleOffre) {
        if (nouvelleOffre == this.offreTarifaire) {
            throw new ChangementOffreImpossibleException(
                    this.reference,
                    this.offreTarifaire.getNom()
            );
        }
        this.offreTarifaireFuture = nouvelleOffre;
    }


    /**
     * Applique le changement d'offre tarifaire précédemment planifié.
     * <p>
     * Si aucune offre future n'est définie, cette méthode n'a aucun effet.
     * </p>
     */

    public void appliquerChangementOffre() {
        if (offreTarifaireFuture != null) {
            this.offreTarifaire = offreTarifaireFuture;
            this.offreTarifaireFuture = null;
        }
    }

    /**
     * Planifie un changement de mode de facturation pour le contrat.
     * <p>
     * Le changement n'est pas immédiat : le nouveau mode est enregistré
     * comme futur et sera appliqué à la date autorisée.
     * </p>
     *
     * @param nouveauMode nouveau mode de facturation souhaité
     * @throws ChangementModeFacturationImpossibleException si le mode demandé
     *         est identique au mode actuel
     */
    public void planifierChangementModeFacturation(ModeFacturation nouveauMode) {
        if (nouveauMode == this.modeFacturation) {
            throw new ChangementModeFacturationImpossibleException(
                    this.reference,
                    this.modeFacturation
            );
        }
        this.modeFacturationFutur = nouveauMode;
    }

    /**
     * Applique le changement de mode de facturation précédemment planifié.
     * <p>
     * Si aucun changement n'était planifié, cette méthode n'a aucun effet.
     * </p>
     */
    public void appliquerChangementModeFacturation() {
        if (modeFacturationFutur != null) {
            this.modeFacturation = modeFacturationFutur;
            this.modeFacturationFutur = null;
        }
    }

    /**
     * Indique si un changement de mode de facturation est autorisé
     * à partir d'une date donnée.
     * <p>
     * Le changement est autorisé uniquement au mois anniversaire
     * du contrat, selon les règles métier définies.
     * </p>
     *
     * @param dateDemande date à laquelle le client effectue la demande
     * @return {@code true} si le changement est autorisé, {@code false} sinon
     * @throws IllegalArgumentException si la date de demande est nulle
     */
    public boolean changementModeFacturationPossible(LocalDate dateDemande) {
        if (dateDemande == null) {
            throw new IllegalArgumentException("La date de demande ne peut pas être nulle");
        }

        LocalDate dateEffet = dateDemande.plusMonths(1);

        return dateEffet.getMonth() == dateSouscription.getMonth();
    }

    /**
     * Calcule la prochaine date autorisée pour un changement
     * de mode de facturation.
     * <p>
     * La date est déterminée en fonction de la date de souscription
     * du contrat et des règles métier en vigueur.
     * </p>
     *
     * @param dateDemande date à laquelle le changement est demandé
     * @return prochaine date possible de changement
     * @throws IllegalArgumentException si la date de demande est nulle
     */
    public LocalDate calculerProchaineDateChangementMode(LocalDate dateDemande) {
        if (dateDemande == null) {
            throw new IllegalArgumentException("La date de demande ne peut pas être nulle");
        }

        LocalDate dateAnniversaire = LocalDate.of(
                dateDemande.getYear(),
                dateSouscription.getMonth(),
                dateSouscription.getDayOfMonth()
        );

        LocalDate moisPrecedent = dateAnniversaire.minusMonths(1);

        if (dateDemande.isAfter(moisPrecedent)) {
            return dateAnniversaire.plusYears(1).minusMonths(1);
        }

        return moisPrecedent;
    }

    /**
     * Indique si un changement d'offre tarifaire est gratuit
     * en fonction de la date de la demande.
     * <p>
     * Un changement d'offre est considéré comme gratuit lorsqu'il
     * intervient lors du mois anniversaire du contrat.
     * </p>
     *
     * @param dateDemande date à laquelle le client effectue la demande
     * @return {@code true} si le changement est gratuit, {@code false} sinon
     * @throws IllegalArgumentException si la date de demande est nulle
     */
    public boolean changementOffreGratuit(LocalDate dateDemande) {
        if (dateDemande == null) {
            throw new IllegalArgumentException("La date de demande ne peut pas être nulle");
        }

        LocalDate dateEffet = dateDemande.plusMonths(1);
        return dateEffet.getMonth() == dateSouscription.getMonth();
    }




    // =====================
    // FINANCIER
    // =====================


    /**
     * Ajoute des frais de changement d'offre en attente de facturation.
     *
     * @param montant montant des frais à ajouter
     */
    public void ajouterFraisChangementOffre(double montant) {
        this.fraisChangementOffreEnAttente += montant;
    }

    /**
     * Réinitialise les frais de changement d'offre en attente.
     */
    public void reinitialiserFraisChangementOffre() {
        this.fraisChangementOffreEnAttente = 0.0;
    }

    /**
     * Ajoute un solde créditeur au contrat.
     *
     * @param soldeCrediteur montant négatif représentant un crédit client
     * @throws IllegalArgumentException si le montant est positif
     */
    public void ajouterSoldeCrediteur(double soldeCrediteur) {
        if (soldeCrediteur > 0) {
            throw new IllegalArgumentException("Le solde créditeur doit être négatif");
        }
        this.soldeCrediteur = soldeCrediteur;
    }

    /**
     * Réinitialise le solde créditeur du contrat.
     */
    public void reinitialiserSoldeCrediteur() {
        this.soldeCrediteur = 0;
    }

    // =====================
    // AFFICHAGE
    // =====================

    /**
     * Retourne une description détaillée du contrat,
     *
     * @return description complète du contrat
     */
    public String getDetails() {
        StringBuilder sb = new StringBuilder();

        sb.append("Référence du contrat : ").append(reference).append("\n");
        sb.append("Statut : ").append(statut).append("\n");
        sb.append("Adresse : ").append(adressePostale).append("\n\n");

        sb.append("Dates\n-----\n");
        sb.append("Date de souscription : ").append(dateSouscription).append("\n");
        if (dateFin != null) {
            sb.append("Date de fin : ").append(dateFin).append("\n");
        }
        sb.append("\n");

        sb.append("Offre tarifaire\n--------------\n");
        sb.append("Offre actuelle : ")
                .append(getLibelleOffreTarifaire(offreTarifaire))
                .append("\n");
        if (offreTarifaireFuture != null) {
            sb.append("Offre à venir : ")
                    .append(getLibelleOffreTarifaire(offreTarifaireFuture))
                    .append("\n");
        }
        sb.append("\n");

        sb.append("Facturation\n-----------\n");
        sb.append("Mode actuel : ")
                .append(getLibelleModeFacturation(modeFacturation))
                .append("\n");
        if (modeFacturationFutur != null) {
            sb.append("Mode à venir : ")
                    .append(getLibelleModeFacturation(modeFacturationFutur))
                    .append("\n");
        }
        sb.append("\n");

        sb.append("Échéancier\n");
        sb.append("----------\n");

        if (echeancier == null) {
            sb.append("Aucun échéancier en cours\n");
        } else {
            sb.append("Date de début : ").append(echeancier.getDateDebut()).append("\n");
            sb.append("Mensualité HT : ")
                    .append(String.format("%.2f €", echeancier.getMontantMensualite()))
                    .append("\n");
            sb.append("Mensualité TTC : ")
                    .append(String.format("%.2f €", echeancier.getMontantMensualiteTTC()))
                    .append("\n");
            sb.append("Mensualités émises : ")
                    .append(echeancier.getMensualitesEmises())
                    .append(" / ")
                    .append(Echeancier.NOMBRE_MENSUALITES)
                    .append("\n");
            sb.append("Mensualités restantes : ")
                    .append(echeancier.getMensualitesRestantes())
                    .append("\n");
            sb.append("Échéancier terminé : ")
                    .append(echeancier.estTermine() ? "Oui" : "Non")
                    .append("\n");
        }

        sb.append("\n");

        sb.append("Factures & relevés\n------------------\n");
        sb.append("Nombre de factures : ").append(factures.size()).append("\n");
        sb.append("Factures impayées : ")
                .append(getFacturesImpayees().size())
                .append("\n");
        sb.append("Nombre de relevés : ").append(releves.size()).append("\n\n");

        sb.append("Financier\n---------\n");
        sb.append("Frais en attente : ")
                .append(String.format("%.2f €", fraisChangementOffreEnAttente))
                .append("\n");
        sb.append("Solde créditeur : ")
                .append(String.format("%.2f €", soldeCrediteur))
                .append("\n");

        return sb.toString();
    }


    /**
     * Retourne un résumé synthétique du contrat,
     * utilisé pour l'affichage en liste.
     *
     * @return résumé du contrat
     */
    public String toResume() {
        return String.format(
                "%s | %s | %s | %s | %s",
                reference,
                adressePostale,
                getLibelleOffreTarifaire(offreTarifaire),
                getLibelleModeFacturation(modeFacturation),
                statut
        );
    }

    // =====================
    // MÉTHODES UTILITAIRES
    // =====================


    private String getLibelleOffreTarifaire(OffreTarifaire offreTarifaire) {
        if (offreTarifaire instanceof OffreClassique) {
            return "Classique";
        } else if (offreTarifaire instanceof OffreHPHC) {
            return "Heures Pleines / Heures Creuses";
        }
        return offreTarifaire.getClass().getSimpleName();
    }

    private String getLibelleModeFacturation(ModeFacturation modeFacturation) {
        return switch (modeFacturation) {
            case REEL -> "Facturation mensuelle au réel";
            case ECHEANCIER -> "Échéancier";
        };
    }
}
