package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.ChangementModeFacturationImpossibleException;
import fr.electronvert.facturation.exception.ContratAvecFacturesImpayeesException;
import fr.electronvert.facturation.exception.RelevesInsuffisantsException;
import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion des contrats d'électricité.
 * <p>
 * Cette classe est responsable de :
 * <ul>
 *   <li>La création et la clôture des contrats</li>
 *   <li>La gestion des échéanciers (création, suppression)</li>
 *   <li>Les changements de mode de facturation et d'offre tarifaire</li>
 *   <li>L'estimation du coût annuel pour la création d'échéanciers</li>
 *   <li>La fourniture de statistiques sur les contrats</li>
 * </ul>
 * Cette classe s'appuie sur le {@link GestionnaireTarifs} pour obtenir
 * les tarifs en vigueur lors des calculs d'estimation.
 *
 *
 * @see Contrat
 * @see Echeancier
 * @see GestionnaireTarifs
 */
public class GestionnaireContrats {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Liste de tous les contrats enregistrés dans le système.
     */
    private final List<Contrat> contrats = new ArrayList<>();

    /**
     * Gestionnaire des tarifs, utilisé pour les estimations de coût.
     */
    private final GestionnaireTarifs gestionnaireTarifs;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un gestionnaire de contrats.
     *
     * @param gestionnaireTarifs gestionnaire des tarifs pour les calculs d'estimation
     *
     * @throws IllegalArgumentException si le gestionnaire de tarifs est null
     */
    public GestionnaireContrats(GestionnaireTarifs gestionnaireTarifs) {
        if (gestionnaireTarifs == null) {
            throw new IllegalArgumentException("Le gestionnaire de tarifs est requis");
        }
        this.gestionnaireTarifs = gestionnaireTarifs;
    }

    // =====================
    // CRÉATION ET CLÔTURE
    // =====================

    /**
     * Crée et enregistre un nouveau contrat pour un client.
     * <p>
     * Si le mode de facturation est ECHEANCIER, un échéancier est
     * automatiquement créé sur la base d'une estimation de consommation.
     * </p>
     *
     * @param client client titulaire du contrat
     * @param adressePostale adresse de fourniture de l'électricité
     * @param offre offre tarifaire souscrite
     * @param modeFacturation mode de facturation choisi
     * @param dateSouscription date de début du contrat
     * @return contrat créé et enregistré
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public Contrat creerContrat(
            Client client,
            String adressePostale,
            OffreTarifaire offre,
            ModeFacturation modeFacturation,
            LocalDate dateSouscription
    ) {
        if (client == null || adressePostale == null || offre == null
                || modeFacturation == null || dateSouscription == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        Contrat contrat = new Contrat(
                client,
                adressePostale,
                offre,
                modeFacturation,
                dateSouscription
        );

        contrats.add(contrat);
        client.ajouterContrat(contrat);

        if (modeFacturation == ModeFacturation.ECHEANCIER) {
            creerEcheancier(contrat, dateSouscription);
        }

        return contrat;
    }

    /**
     * Clôture un contrat à une date donnée.
     * <p>
     * Un contrat ne peut être clôturé que s'il :
     * <ul>
     *   <li>Est actuellement actif</li>
     *   <li>N'a aucune facture impayée</li>
     * </ul>
     *
     * @param contrat contrat à clôturer
     * @param dateCloture date de clôture
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     * @throws IllegalStateException si le contrat est déjà clôturé
     * @throws ContratAvecFacturesImpayeesException si le contrat a des factures impayées
     */
    public void cloturerContrat(Contrat contrat, LocalDate dateCloture) {
        if (contrat == null || dateCloture == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!contrat.estActif()) {
            throw new IllegalStateException("Le contrat est déjà clôturé");
        }

        if (contrat.aDesFacturesImpayees()) {
            throw new ContratAvecFacturesImpayeesException(
                    contrat.getReference(),
                    contrat.getFacturesImpayees()
            );
        }

        contrat.cloturer(dateCloture);

    }

    // =====================
    // GESTION DES ÉCHÉANCIERS
    // =====================

    /**
     * Crée un nouvel échéancier pour le contrat donné.
     * <p>
     * L'échéancier est basé sur une estimation du coût annuel,
     * calculée à partir de la consommation réelle si disponible,
     * ou d'une estimation par défaut (4500 kWh/an) sinon.
     * </p>
     *
     * @param contrat contrat pour lequel créer l'échéancier
     *
     * @throws IllegalArgumentException si le contrat est null
     */
    public void creerEcheancier(Contrat contrat, LocalDate dateDebut) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        double estimationAnnuelleHT = estimerCoutAnnuel(
                contrat,
                contrat.getDateSouscription()
        );

        Echeancier echeancier = new Echeancier(
                dateDebut, //
                estimationAnnuelleHT
        );

        contrat.attacherNouvelEcheancier(echeancier);
        contrat.ajouterEcheancier(echeancier);
    }

    /**
     * Estime le coût annuel du contrat pour la création d'un échéancier.
     * <p>
     * L'estimation se base sur :
     * <ul>
     *   <li>La consommation réelle de l'année précédente si disponible</li>
     *   <li>Une estimation par défaut (4500 kWh/an) sinon</li>
     * </ul>
     * Le calcul inclut le coût de l'électricité et l'abonnement annuel.
     * </p>
     *
     * @param contrat contrat à estimer
     * @param dateEstimation date de référence pour obtenir le tarif
     * @return coût annuel estimé HT
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    private double estimerCoutAnnuel(Contrat contrat, LocalDate dateEstimation) {
        if (contrat == null || dateEstimation == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        // 1️⃣ Estimation de la consommation annuelle
        Map<TypeConso, Double> consommationAnnuelle;

        try {
            consommationAnnuelle = calculerConsommationAnneePrecedente(contrat, dateEstimation);
        } catch (RelevesInsuffisantsException e) {
            consommationAnnuelle = estimerConsommationParDefaut(contrat);
        }

        // 2️⃣ Tarif en vigueur
        Tarif tarif = gestionnaireTarifs.getTarifActif(dateEstimation);

        // 3️⃣ Calcul du coût de l'électricité via l'offre
        double coutElectriciteHT =
                contrat.getOffreTarifaire()
                        .calculerCoutElectricite(consommationAnnuelle, tarif);

        // 4️⃣ Abonnement annuel
        double abonnementAnnuelHT =
                contrat.getOffreTarifaire()
                        .calculerCoutAbonnementAnnuel(tarif);

        return coutElectriciteHT + abonnementAnnuelHT;
    }

    /**
     * Estime la consommation annuelle par défaut selon l'offre tarifaire.
     * <p>
     * Valeur par défaut : 4500 kWh/an, répartis selon l'offre :
     * <ul>
     *   <li>Classique : 100% en TOTAL</li>
     *   <li>HP/HC : 60% en HP, 40% en HC</li>
     * </ul>
     * </p>
     *
     * @param contrat contrat pour lequel estimer la consommation
     * @return map des consommations estimées par type
     *
     * @throws IllegalArgumentException si le contrat est null
     * @throws IllegalStateException si l'offre tarifaire est inconnue
     */
    private Map<TypeConso, Double> estimerConsommationParDefaut(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        double consoMoyenneAnnuelle = 4500.0;

        if (contrat.getOffreTarifaire() instanceof OffreClassique) {
            return Map.of(TypeConso.TOTAL, consoMoyenneAnnuelle);
        }

        if (contrat.getOffreTarifaire() instanceof OffreHPHC) {
            return Map.of(
                    TypeConso.HP, consoMoyenneAnnuelle * 0.6,
                    TypeConso.HC, consoMoyenneAnnuelle * 0.4
            );
        }

        throw new IllegalStateException("Offre tarifaire inconnue");
    }

    /**
     * Calcule la consommation réelle de l'année précédente.
     * <p>
     * Utilise le premier et le dernier relevé du contrat pour calculer
     * la consommation totale.
     * </p>
     *
     * @param contrat contrat dont calculer la consommation
     * @return map des consommations réelles par type
     *
     * @throws IllegalArgumentException si le contrat est null
     * @throws IllegalStateException si le contrat a moins de 2 relevés
     */
    /**
     * Calcule la consommation réelle sur les 12 derniers mois.
     * <p>
     * La consommation est calculée à partir des relevés compris
     * entre {@code dateReference.minusYears(1)} et {@code dateReference}.
     * </p>
     *
     * @param contrat contrat dont calculer la consommation
     * @param dateReference date de référence (fin de la période)
     * @return map des consommations réelles par type
     *
     * @throws IllegalArgumentException si le contrat ou la date sont nuls
     * @throws IllegalStateException si les relevés sont insuffisants
     */
    private Map<TypeConso, Double> calculerConsommationAnneePrecedente(
            Contrat contrat,
            LocalDate dateReference
    ) {
        if (contrat == null || dateReference == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        LocalDate debutPeriode = dateReference.minusYears(1);

        List<Releve> relevesPeriode = contrat.getReleves().stream()
                .filter(r -> !r.getDateDeReleve().isBefore(debutPeriode)
                        && !r.getDateDeReleve().isAfter(dateReference))
                .sorted()
                .toList();

        if (relevesPeriode.size() < 2) {
            throw new RelevesInsuffisantsException(
                    contrat.getReference(), relevesPeriode.size()
            );
        }

        Releve debut = relevesPeriode.get(0);
        Releve fin = relevesPeriode.get(relevesPeriode.size() - 1);

        return fin.calculerConsommation(debut);
    }


    // =====================
    // CHANGEMENTS D'OFFRE ET DE MODE
    // =====================

    /**
     * Enregistre une demande de changement de mode de facturation.
     * <p>
     * Le changement est planifié et sera appliqué le 6 du mois suivant
     * par le {@link SimulateurDate}.
     * </p>
     *
     * @param contrat contrat concerné
     * @param nouveauMode nouveau mode de facturation souhaité
     * @param dateDemande date de la demande
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     * @throws ChangementModeFacturationImpossibleException si le changement
     *         ne peut pas être effectué à cette date
     */
    public void demanderChangementModeFacturation(
            Contrat contrat,
            ModeFacturation nouveauMode,
            LocalDate dateDemande
    ) {
        if (contrat == null || nouveauMode == null || dateDemande == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!contrat.changementModeFacturationPossible(dateDemande)) {
            LocalDate prochaineDate = contrat.calculerProchaineDateChangementMode(dateDemande);

            throw new ChangementModeFacturationImpossibleException(
                    contrat.getReference(),
                    contrat.getModeFacturation(),
                    nouveauMode,
                    prochaineDate
            );
        }

        contrat.planifierChangementModeFacturation(nouveauMode);
    }

    /**
     * Enregistre une demande de changement d'offre tarifaire.
     * <p>
     * Le changement est planifié et sera appliqué le 1er du mois suivant
     * par le {@link SimulateurDate}. Des frais peuvent être appliqués si
     * le changement ne se fait pas pendant le mois anniversaire du contrat.
     * </p>
     *
     * @param contrat contrat concerné
     * @param nouvelleOffre nouvelle offre tarifaire souhaitée
     * @param dateDemande date de la demande
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public void demanderChangementOffreTarifaire(
            Contrat contrat,
            OffreTarifaire nouvelleOffre,
            LocalDate dateDemande
    ) {
        if (contrat == null || nouvelleOffre == null || dateDemande == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!contrat.changementOffreGratuit(dateDemande)) {
            contrat.ajouterFraisChangementOffre(
                    OffreTarifaire.getFraisChangementOffreHT()
            );
        }

        contrat.planifierChangementOffreTarifaire(nouvelleOffre);
    }

    /**
     * Applique les changements planifiés de mode de facturation et d'offre.
     * <p>
     * Cette méthode est appelée automatiquement par le {@link SimulateurDate}.
     * Elle gère la création ou suppression d'échéanciers selon le nouveau mode :
     * <ul>
     *   <li>REEL → ECHEANCIER : crée un nouvel échéancier</li>
     *   <li>ECHEANCIER → REEL : supprime l'échéancier actuel</li>
     * </ul>
     *
     * @param contrat contrat sur lequel appliquer les changements
     *
     * @throws IllegalArgumentException si le contrat est null
     */
    public void appliquerChangementsPlanifiesModeFacturation(Contrat contrat, LocalDate dateApplication) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        ModeFacturation ancienMode = contrat.getModeFacturation();

        contrat.appliquerChangementModeFacturation();
        contrat.appliquerChangementOffre();

        ModeFacturation nouveauMode = contrat.getModeFacturation();

        // Passage à l'échéancier : créer un échéancier
        if (ancienMode != ModeFacturation.ECHEANCIER
                && nouveauMode == ModeFacturation.ECHEANCIER) {
            creerEcheancier(contrat, dateApplication);
        }

        // Passage au mode réel : supprimer l'échéancier
        if (ancienMode == ModeFacturation.ECHEANCIER
                && nouveauMode != ModeFacturation.ECHEANCIER) {
            contrat.supprimerEcheancier();
        }
    }

    // =====================
    // RECHERCHE
    // =====================

    /**
     * Recherche un contrat par sa référence.
     *
     * @param reference référence du contrat recherché (insensible à la casse)
     * @return contrat trouvé ou {@code null} si aucun contrat ne correspond
     */
    public Contrat rechercherParReference(String reference) {
        if (reference == null) {
            return null;
        }

        for (Contrat contrat : contrats) {
            if (contrat.getReference().equalsIgnoreCase(reference)) {
                return contrat;
            }
        }

        return null;
    }

    // =====================
    // ACCÈS AUX DONNÉES
    // =====================

    /**
     * Retourne la liste de tous les contrats enregistrés.
     *
     * @return copie non modifiable de la liste des contrats
     */
    public List<Contrat> getContrats() {
        return List.copyOf(contrats);
    }

    public List<Contrat> getContratsClient(Client client) {
        List<Contrat> contratsClient = new ArrayList<>();
        for(Contrat contrat : contrats) {
            if (contrat.getClient().equals(client)) {
                contratsClient.add(contrat);
            }
        }
        return contratsClient;
    }

    /**
     * Retourne la liste des contrats actuellement actifs.
     *
     * @return liste des contrats dont le statut est ACTIF
     */
    public List<Contrat> getContratsActifs() {
        List<Contrat> contratsActifs = new ArrayList<>();
        for (Contrat contrat : contrats) {
            if (contrat.estActif()) {
                contratsActifs.add(contrat);
            }
        }
        return contratsActifs;
    }

    public List<Contrat> getContratsAFacturer() {
        List<Contrat> contratsAFacturers = new ArrayList<>();
        for (Contrat contrat : contrats) {
            if(!contrat.estFacturationTerminee()){
                contratsAFacturers.add(contrat);
            }
        }
        return contratsAFacturers;
    }


    // =====================
    // STATISTIQUES
    // =====================

    /**
     * Retourne le nombre de contrats actuellement actifs.
     *
     * @return nombre de contrats actifs
     */
    public long getNombreContratsActifs() {
        return getContratsActifs().size();
    }

    /**
     * Retourne le nombre de contrats clôturés.
     *
     * @return nombre de contrats clôturés
     */
    public long getNombreContratsClotures() {
        return contrats.stream()
                .filter(c -> !c.estActif())
                .count();
    }

    /**
     * Retourne le nombre de contrats actifs avec l'offre Classique.
     *
     * @return nombre de contrats en offre Classique
     */
    public long getNombreContratsOffreClassique() {
        return getContratsActifs().stream()
                .filter(c -> c.getOffreTarifaire() instanceof OffreClassique)
                .count();
    }

    /**
     * Retourne le nombre de contrats actifs avec l'offre HP/HC.
     *
     * @return nombre de contrats en offre HP/HC
     */
    public long getNombreContratsOffreHPHC() {
        return getContratsActifs().stream()
                .filter(c -> c.getOffreTarifaire() instanceof OffreHPHC)
                .count();
    }

    /**
     * Retourne le nombre de contrats actifs en mode facturation réelle.
     *
     * @return nombre de contrats en mode REEL
     */
    public long getNombreContratsModeReel() {
        return getContratsActifs().stream()
                .filter(c -> c.getModeFacturation() == ModeFacturation.REEL)
                .count();
    }

    /**
     * Retourne le nombre de contrats actifs en mode échéancier.
     *
     * @return nombre de contrats en mode ECHEANCIER
     */
    public long getNombreContratsModeEcheancier() {
        return getContratsActifs().stream()
                .filter(c -> c.getModeFacturation() == ModeFacturation.ECHEANCIER)
                .count();
    }
}