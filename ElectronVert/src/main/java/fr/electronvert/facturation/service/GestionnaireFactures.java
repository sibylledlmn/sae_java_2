package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.ContratInactifException;
import fr.electronvert.facturation.exception.FactureDejaExistantePourMoisException;
import fr.electronvert.facturation.exception.RelevesInsuffisantsException;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.Echeancier;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreTarifaire;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.facture.TauxTVA;
import fr.electronvert.facturation.model.facture.TypeFacture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service métier responsable de la création, du suivi et de la régularisation
 * des factures.
 * <p>
 * Ce gestionnaire :
 * <ul>
 *   <li>Crée les factures mensuelles pour les contrats en mode réel</li>
 *   <li>Crée les factures de régularisation (fin d'échéancier ou clôture)</li>
 *   <li>Gère les échéances et les passages en impayée</li>
 *   <li>Calcule les régularisations (coût réel - mensualités payées)</li>
 *   <li>Génère les références uniques de factures</li>
 *   <li>Fournit des statistiques globales sur la facturation</li>
 * </ul>
 * Il s'appuie sur les relevés, les contrats et les tarifs en vigueur
 * pour garantir une facturation cohérente et conforme aux règles métier.
 *
 *
 * @see Facture
 * @see Contrat
 * @see Echeancier
 * @see GestionnaireTarifs
 */
public class GestionnaireFactures {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Gestionnaire des tarifs, utilisé pour obtenir les tarifs actifs.
     */
    private final GestionnaireTarifs gestionnaireTarifs;

    /**
     * Liste de toutes les factures enregistrées dans le système.
     */
    private final List<Facture> factures = new ArrayList<>();

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un gestionnaire de factures.
     *
     * @param gestionnaireTarifs gestionnaire des tarifs nécessaire pour les calculs
     *
     * @throws IllegalArgumentException si le gestionnaire de tarifs est null
     */
    public GestionnaireFactures(GestionnaireTarifs gestionnaireTarifs) {
        if (gestionnaireTarifs == null) {
            throw new IllegalArgumentException("Le gestionnaire de tarifs est requis");
        }
        this.gestionnaireTarifs = gestionnaireTarifs;
    }

    // =====================
    // CRÉATION DE FACTURES MENSUELLES
    // =====================

    /**
     * Crée une facture mensuelle pour un contrat en mode facturation réelle.
     * <p>
     * La facture est calculée à partir :
     * <ul>
     *   <li>Du dernier et de l'avant-dernier relevé</li>
     *   <li>Du tarif en vigueur à la date d'émission</li>
     *   <li>Des frais de changement d'offre en attente si présents</li>
     * </ul>
     * Si la facture est générée après la clôture du contrat, le contrat est
     * marqué comme facturé définitivement.
     *
     *
     * @param contrat contrat pour lequel créer la facture
     * @param dateEmission date d'émission de la facture (généralement le 5 du mois)
     * @return facture mensuelle créée et ajoutée au contrat
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     * @throws ContratInactifException si le contrat n'est pas actif et sans relevé de clôture
     * @throws IllegalStateException si aucune offre tarifaire n'est associée au contrat
     * @throws FactureDejaExistantePourMoisException si une facture mensuelle existe déjà pour ce mois
     * @throws RelevesInsuffisantsException si le contrat n'a pas au moins 2 relevés
     */
    public Facture creerFactureMensuelle(
            Contrat contrat,
            LocalDate dateEmission
    ) {
        // Validations
        if (contrat == null || dateEmission == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!contrat.estActif() && contrat.getDernierReleve().getTypeReleve() != TypeReleve.CLOTURE) {
            throw new ContratInactifException(
                    contrat.getReference(),
                    contrat.getDateFin()
            );
        }

        OffreTarifaire offreTarifaire = contrat.getOffreTarifaire();
        if (offreTarifaire == null) {
            throw new IllegalStateException("Aucune offre tarifaire associée au contrat");
        }

        // Vérification de l'unicité
        LocalDate moisFacture = dateEmission.withDayOfMonth(1);
        Facture factureExistante = rechercherFacturePourMois(
                contrat,
                TypeFacture.MENSUELLE,
                moisFacture
        );

        if (factureExistante != null) {
            throw new FactureDejaExistantePourMoisException(
                    contrat.getReference(),
                    TypeFacture.MENSUELLE,
                    moisFacture,
                    factureExistante.getReference()
            );
        }

        // -------- Relevés --------
        Releve dernier = contrat.getDernierReleve();
        Releve precedent = contrat.getAvantDernierReleve();

        int nbReleves = (dernier != null ? 1 : 0) + (precedent != null ? 1 : 0);
        if (dernier == null || precedent == null) {
            throw new RelevesInsuffisantsException(
                    contrat.getReference(),
                    nbReleves
            );
        }

        // -------- Tarif actif --------
        Tarif tarifActif = gestionnaireTarifs.getTarifActif(dateEmission);

        // -------- Consommation --------
        Map<TypeConso, Double> consommations =
                offreTarifaire.calculerConsommation(precedent, dernier);

        // -------- Coûts HT --------
        double coutElectriciteHT =
                offreTarifaire.calculerCoutElectricite(consommations, tarifActif);

        double coutAbonnementMensuelHT =
                offreTarifaire.calculerCoutAbonnementMensuel(tarifActif);

        // ---------- TVA ----------
        double montantHT = coutElectriciteHT + coutAbonnementMensuelHT;
        double montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);

        // ---------- Frais de changement d'offre ----------
        boolean contientFraisChangementOffre = false;

        if (contrat.getFraisChangementOffreEnAttente() > 0) {
            double fraisHT = contrat.getFraisChangementOffreEnAttente();
            double fraisTVA = TauxTVA.NORMAL.calculerMontantTVA(fraisHT);

            montantHT += fraisHT;
            montantTVA += fraisTVA;

            contrat.reinitialiserFraisChangementOffre();
            contientFraisChangementOffre = true;
        }

        double montantTTC = montantHT + montantTVA;

        // ---------- Référence ----------
        String reference = genererReferenceFacture(
                contrat,
                TypeFacture.MENSUELLE,
                moisFacture
        );

        // ---------- Création facture ----------
        Facture facture = new Facture(
                contrat,
                dateEmission,
                reference,
                TypeFacture.MENSUELLE
        );

        facture.definirMontants(montantHT, montantTVA, montantTTC);

        if (contientFraisChangementOffre) {
            facture.marquerPresenceFraisChangementOffre();
        }

        factures.add(facture);

        if (!contrat.estActif()
                && contrat.getDernierReleve().getTypeReleve() == TypeReleve.CLOTURE) {
            contrat.marquerFacturationTerminee();
        }

        return facture;
    }

    // =====================
    // GESTION DES ÉCHÉANCES
    // =====================

    /**
     * Vérifie les échéances de toutes les factures et passe en impayée
     * celles dont la date d'échéance est dépassée.
     * <p>
     * Cette méthode est appelée quotidiennement par le {@link SimulateurDate}.
     *
     *
     * @param date date de référence pour la vérification
     *
     * @throws IllegalArgumentException si la date est null
     */
    public void verifierEcheances(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }

        for (Facture facture : factures) {
            if (facture.getStatut() == StatutFacture.EMISE
                    && date.isAfter(facture.getDateEcheance())) {
                facture.passerEnImpayee(date);
            }
        }
    }

    // =====================
    // FACTURES DE RÉGULARISATION
    // =====================

    /**
     * Crée une facture de régularisation pour un contrat.
     * <p>
     * La régularisation peut être :
     * <ul>
     *   <li><strong>Fin d'échéancier</strong> : après 11 mensualités payées</li>
     *   <li><strong>Clôture anticipée</strong> : à la clôture d'un contrat en mode échéancier</li>
     * </ul>
     * Le montant de régularisation peut être :
     * <ul>
     *   <li><strong>Positif</strong> : facture à payer (consommation > estimations)</li>
     *   <li><strong>Négatif</strong> :
     *     <ul>
     *       <li>Si contrat clôturé : remboursement au client</li>
     *       <li>Si contrat actif : solde créditeur pour futures factures</li>
     *     </ul>
     *   </li>
     * </ul>
     * L'échéancier actuel est terminé après la création de la facture.
     *
     *
     * @param contrat contrat pour lequel créer la régularisation
     * @param dateEmission date d'émission de la facture
     * @return facture de régularisation créée
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public Facture creerFactureRegularisation(
            Contrat contrat,
            LocalDate dateEmission
    ) {
        if (contrat == null || dateEmission == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        double regularisation;

        if (!contrat.estActif()) {
            regularisation = calculerRegularisationCloture(contrat);
        } else {
            regularisation = calculerRegularisationFinEcheancier(contrat);
        }

        LocalDate moisFacture = dateEmission.withDayOfMonth(1);

        String reference = genererReferenceFacture(
                contrat,
                TypeFacture.REGULARISATION,
                moisFacture
        );

        Facture facture = new Facture(
                contrat,
                dateEmission,
                reference,
                TypeFacture.REGULARISATION
        );

        // régularisation négative
        if (regularisation < 0) {
            if (!contrat.estActif()) {
                // Contrat clôturé : facture négative remboursée
                double montantHT = regularisation;
                double montantTVA = 0;
                double montantTTC = regularisation;

                facture.definirMontants(montantHT, montantTVA, montantTTC);
                facture.marquerCommePayee();
            } else {
                // Contrat actif : solde créditeur
                contrat.ajouterSoldeCrediteur(regularisation);
                facture.definirMontants(0, 0, 0);
                facture.marquerCommePayee();
            }
        }
        // régularisation positive
        else {
            double montantHT = regularisation;
            double montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);
            double montantTTC = montantHT + montantTVA;

            facture.definirMontants(montantHT, montantTVA, montantTTC);
        }

        factures.add(facture);

        // Fin de l'échéancier si présent
        if (contrat.getEcheancier() != null) {
            contrat.getEcheancier().terminer();
        }

        return facture;
    }

    /**
     * Régularise un contrat actif en fin d'échéancier (après 11 mensualités).
     * <p>
     * Cette méthode est appelée automatiquement par le {@link SimulateurDate}
     * lorsque l'échéancier doit déclencher une régularisation.
     * </p>
     *
     * @param contrat contrat à régulariser
     * @param date date de la régularisation
     * @return facture de régularisation créée
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     * @throws IllegalStateException si l'échéancier ne doit pas déclencher de régularisation
     */
    public Facture regulariserFinEcheancier(Contrat contrat, LocalDate date) {
        if (contrat == null || date == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        calculerRegularisationFinEcheancier(contrat); // Validation
        return creerFactureRegularisation(contrat, date);
    }

    /**
     * Régularise un contrat avec échéancier lors d'une clôture anticipée.
     * <p>
     * Calcule le solde entre le coût réel depuis le début de l'échéancier
     * et les mensualités déjà payées, puis génère une facture de régularisation.
     * Marque ensuite la facturation comme terminée.
     * </p>
     *
     * @param contrat contrat clôturé à régulariser
     * @param date date de la régularisation
     * @return facture de régularisation créée
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     * @throws IllegalStateException si le contrat est en mode réel ou sans relevé de clôture
     */
    public Facture regulariserClotureEcheancier(Contrat contrat, LocalDate date) {
        if (contrat == null || date == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        Releve dernier = contrat.getDernierReleve();

        if (contrat.getModeFacturation() == ModeFacturation.REEL) {
            throw new IllegalStateException(
                    "Aucune régularisation n'est nécessaire pour un contrat en facturation réelle"
            );
        }

        if (dernier == null || dernier.getTypeReleve() != TypeReleve.CLOTURE) {
            throw new IllegalStateException(
                    "Un relevé de clôture est requis pour la régularisation d'un contrat clôturé"
            );
        }

        calculerRegularisationCloture(contrat);
        Facture facture = creerFactureRegularisation(contrat, date);
        contrat.marquerFacturationTerminee();

        return facture;
    }

    // =====================
    // CALCULS DE RÉGULARISATION
    // =====================

    /**
     * Calcule le montant de régularisation pour un contrat en fin d'échéancier.
     * <p>
     * Formule : Coût réel HT - Mensualités payées HT
     * </p>
     *
     * @param contrat contrat pour lequel calculer la régularisation
     * @return montant de régularisation (positif = à payer, négatif = en faveur du client)
     *
     * @throws IllegalArgumentException si le contrat est null
     * @throws IllegalStateException si l'échéancier ne doit pas déclencher de régularisation
     *         ou si les relevés sont insuffisants
     */
    public double calculerRegularisationFinEcheancier(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        Echeancier echeancier = contrat.getEcheancier();

        if (echeancier == null || echeancier.peutEmettreMensualite()) {
            throw new IllegalStateException(
                    "L'échéancier ne permet pas de déclencher une régularisation"
            );
        }

        double montantReel = calculerCoutReel(contrat, echeancier);
        double montantDejaPaye = calculerMontantMensualites(echeancier);

        return montantReel - montantDejaPaye;
    }



    /**
     * Calcule le montant de régularisation pour un contrat avec échéancier en cours de clôture.
     * <p>
     * Formule : Coût réel HT - Mensualités payées HT
     * </p>
     * <p>
     * Cette méthode peut être appelée avant que le contrat soit marqué comme clôturé,
     * pourvu qu'un relevé de clôture existe.
     * </p>
     *
     * @param contrat contrat pour lequel calculer la régularisation
     * @return montant de régularisation (positif = à payer, négatif = en faveur du client)
     *
     * @throws IllegalArgumentException si le contrat est null
     * @throws IllegalStateException si les relevés sont insuffisants
     */
    public double calculerRegularisationCloture(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        Echeancier echeancier = contrat.getEcheancier();
        if (echeancier == null) {
            return 0.0;
        }

        double montantReel = calculerCoutReel(contrat, echeancier);
        double montantDejaPaye = calculerMontantMensualites(echeancier);

        return montantReel - montantDejaPaye;
    }


    /**
     * Calcule le coût réel HT de l'électricité sur la période couverte
     * par un échéancier.
     * <p>
     * Le calcul s'effectue entre :
     * <ul>
     *   <li>le dernier relevé AVANT ou À la date de début de l'échéancier</li>
     *   <li>le dernier relevé disponible (ou relevé de clôture)</li>
     * </ul>
     * Cette approche garantit un calcul correct même si aucun relevé
     * n'existe exactement à la date de début de l'échéancier.
     * </p>
     *
     * @param contrat contrat concerné
     * @param echeancier échéancier définissant la période de calcul
     * @return coût réel total HT sur la période
     *
     * @throws IllegalArgumentException si un paramètre est null
     * @throws IllegalStateException si les relevés sont insuffisants
     */
    /**
     * Calcule le coût réel HT de l'électricité entre les deux derniers relevés.
     * Le dernier relevé doit être un relevé de régularisation ou de clôture.
     */
    /**
     * Calcule le coût réel HT de l'électricité pour un échéancier,
     * à partir du relevé de début d'échéancier et du relevé de régularisation
     * (ou de clôture).
     */
    private double calculerCoutReel(
            Contrat contrat,
            Echeancier echeancier
    ) {
        if (contrat == null || echeancier == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        // 🔹 Relevé de début = relevé à la date de début de l’échéancier
        Releve releveDebut = contrat.trouverReleveADate(
                echeancier.getDateDebut()
        );

        // 🔹 Relevé de fin = DERNIER relevé du contrat
        Releve releveFin = contrat.getDernierReleve();

        if (releveFin == null) {
            throw new IllegalStateException(
                    "Aucun relevé de fin disponible pour la régularisation"
            );
        }

        Map<TypeConso, Double> consommation =
                contrat.getOffreTarifaire()
                        .calculerConsommation(releveDebut, releveFin);

        Tarif tarif =
                gestionnaireTarifs.getTarifActif(releveFin.getDateDeReleve());

        return contrat.getOffreTarifaire()
                .calculerCoutElectricite(consommation, tarif)
                + contrat.getOffreTarifaire()
                .calculerCoutAbonnementAnnuel(tarif);
    }








    /**
     * Calcule le montant total des mensualités payées pour un échéancier.
     *
     * @param echeancier échéancier dont calculer le montant payé
     * @return montant total HT des mensualités émises
     *
     * @throws IllegalArgumentException si l'échéancier est null
     */
    private double calculerMontantMensualites(Echeancier echeancier) {
        if (echeancier == null) {
            throw new IllegalArgumentException("Échéancier inexistant");
        }

        return echeancier.getMensualitesEmises()
                * echeancier.getMontantMensualite();
    }

    // =====================
    // ACCÈS AUX DONNÉES
    // =====================

    /**
     * Retourne la liste de toutes les factures enregistrées.
     *
     * @return liste non modifiable des factures
     */
    public List<Facture> getFactures() {
        return Collections.unmodifiableList(factures);
    }

    /**
     * Retourne la liste des factures actuellement impayées.
     *
     * @return liste des factures avec le statut IMPAYEE
     */
    public List<Facture> getFacturesImpayees() {
        List<Facture> resultat = new ArrayList<>();
        for (Facture f : factures) {
            if (f.getStatut() == StatutFacture.IMPAYEE) {
                resultat.add(f);
            }
        }
        return resultat;
    }

    /**
     * Retourne toutes les factures d'un client donné.
     *
     * @param client dont on récupère les factures
     * @return liste des factures du client (tous ses contrats confondus)
     *
     * @throws IllegalArgumentException si le client est null
     */
    public List<Facture> getFacturesParClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être nul");
        }

        List<Facture> resultat = new ArrayList<>();
        for (Facture f : factures) {
            if (f.getContrat().getClient().equals(client)) {
                resultat.add(f);
            }
        }
        return resultat;
    }

    // =====================
    // RECHERCHE
    // =====================

//    /**
//     * Recherche une facture par sa référence.
//     *
//     * @param reference référence de la facture recherchée
//     * @return facture trouvée ou {@code null} si aucune facture ne correspond
//     */
//    public Facture rechercherParReference(String reference) {
//        if (reference == null) {
//            return null;
//        }
//
//        for (Facture f : factures) {
//            if (f.getReference().equals(reference)) {
//                return f;
//            }
//        }
//        return null;
//    }

    /**
     * Recherche une facture existante pour un contrat, un type et un mois donnés.
     *
     * @param contrat contrat concerné
     * @param type type de facture
     * @param mois mois de la facture (1er du mois)
     * @return facture trouvée ou {@code null} si aucune ne correspond
     */
    private Facture rechercherFacturePourMois(
            Contrat contrat,
            TypeFacture type,
            LocalDate mois
    ) {
        return factures.stream()
                .filter(f -> f.getContrat().equals(contrat)
                        && f.getType() == type
                        && f.getDateEmission().withDayOfMonth(1).equals(mois))
                .findFirst()
                .orElse(null);
    }

    // =====================
    // GÉNÉRATION DE RÉFÉRENCES
    // =====================

    /**
     * Génère une référence unique pour une facture.
     * <p>
     * Format : {@code FACT-[ID_CONTRAT]-[TYPE]-[ANNÉE]-[MOIS]-[NUMÉRO]}
     * <br>
     * Exemple : {@code FACT-123-MENSUELLE-2025-04-001}
     * </p>
     *
     * @param contrat contrat concerné
     * @param type type de facture
     * @param dateEmission date d'émission pour l'année et le mois
     * @return référence unique générée
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public String genererReferenceFacture(
            Contrat contrat,
            TypeFacture type,
            LocalDate dateEmission
    ) {
        if (contrat == null || type == null || dateEmission == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        String base =
                "FACT-"
                        + contrat.getReference()
                        + "-"
                        + type.name()
                        + "-"
                        + dateEmission.getYear()
                        + "-"
                        + (dateEmission.getMonthValue() < 10
                        ? "0" + dateEmission.getMonthValue()
                        : dateEmission.getMonthValue());

        long compteur = factures.stream()
                .filter(f -> f.getReference().startsWith(base))
                .count();

        long numero = compteur + 1;

        String suffixe;
        if (numero < 10) {
            suffixe = "00" + numero;
        } else if (numero < 100) {
            suffixe = "0" + numero;
        } else {
            suffixe = String.valueOf(numero);
        }

        return base + "-" + suffixe;
    }

    // =====================
    // STATISTIQUES
    // =====================

    /**
     * Retourne le nombre de factures impayées.
     *
     * @return nombre de factures avec le statut IMPAYEE
     */
    public long getNombreFacturesImpayees() {
        return getFacturesImpayees().size();
    }

    /**
     * Calcule le montant total des factures impayées.
     *
     * @return somme des montants TTC de toutes les factures impayées
     */
    public double getMontantTotalImpayes() {
        return getFacturesImpayees().stream()
                .mapToDouble(Facture::getMontantTTC)
                .sum();
    }

    /**
     * Retourne le nombre de clients distincts ayant au moins une facture impayée.
     *
     * @return nombre de clients avec des impayés
     */
    public long getNombreClientsAvecImpayes() {
        return getFacturesImpayees().stream()
                .map(f -> f.getContrat().getClient())
                .distinct()
                .count();
    }

    /**
     * Retourne le nombre de factures payées.
     *
     * @return nombre de factures avec le statut PAYEE
     */
    public long getNombreFacturesPayees() {
        return factures.stream()
                .filter(f -> f.getStatut() == StatutFacture.PAYEE)
                .count();
    }

    /**
     * Retourne le nombre de factures émises (en attente de paiement).
     *
     * @return nombre de factures avec le statut EMISE
     */
    public long getNombreFacturesEmises() {
        return factures.stream()
                .filter(f -> f.getStatut() == StatutFacture.EMISE)
                .count();
    }
}