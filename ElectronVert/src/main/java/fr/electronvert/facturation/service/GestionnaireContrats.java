package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestionnaireContrats {

    private final List<Contrat> contrats = new ArrayList<>();
    private final GestionnaireTarifs gestionnaireTarifs;


    public GestionnaireContrats(GestionnaireTarifs gestionnaireTarifs) {
        this.gestionnaireTarifs = gestionnaireTarifs;
    }


    public List<Contrat> getContrats() {
        return List.copyOf(contrats);
    }

    public Contrat creerContrat(Client client, String adressePostale,
                                OffreTarifaire offre,
                                ModeFacturation modeFacturation,
                                LocalDate dateSouscription) {

        Contrat contrat = new Contrat(client, adressePostale, offre, modeFacturation, dateSouscription);

        contrats.add(contrat);

        if (modeFacturation == ModeFacturation.ECHEANCIER) {
            creerEcheancierInitial(contrat);
        }

        return contrat;
    }


    public List<Contrat> getContratsActifs(){
        List<Contrat> contratsActifs = new ArrayList<>();
        for (Contrat contrat : contrats) {
            if(contrat.estActif()){
                contratsActifs.add(contrat);
            }
        }
        return contratsActifs;
    }



    // =====================
    // ÉCHÉANCIER
    // =====================

    // TODO : reovir le local date now avec mon simulateur de date

    private void creerEcheancierInitial(Contrat contrat) {

        double estimationAnnuelleHT = estimerCoutAnnuel(contrat);

        Echeancier echeancier = new Echeancier(
                LocalDate.now(),
                estimationAnnuelleHT
        );

        contrat.attacherNouvelEcheancier(echeancier);
    }

    // =====================
    // CHANGEMENT DE MODE
    // =====================

    public void changerModeFacturation(Contrat contrat,
                                       ModeFacturation nouveauMode) {

        ModeFacturation ancienMode = contrat.getModeFacturation();
        contrat.changerModeFacturation(nouveauMode);

        if (ancienMode != ModeFacturation.ECHEANCIER
                && nouveauMode == ModeFacturation.ECHEANCIER) {
            creerEcheancierInitial(contrat);
        }

        if (ancienMode == ModeFacturation.ECHEANCIER
                && nouveauMode != ModeFacturation.ECHEANCIER) {
            contrat.supprimerEcheancier();
        }
    }

    public void changerOffreTarifaire(Contrat contrat,
                                      OffreTarifaire nouvelleOffre) {

        contrat.changerOffreTarifaire(nouvelleOffre);
        contrat.ajouterFraisChangementOffre(nouvelleOffre.getFraisChangementOffreHT());
    }

    // =====================
    // ESTIMATION ANNUELLE
    // =====================

    private double estimerCoutAnnuel(Contrat contrat) {

        LocalDate dateEstimation = LocalDate.now();

        // 1️⃣ Estimation de la consommation annuelle
        Map<TypeConso, Double> consommationAnnuelle;

        if (contrat.getReleves().isEmpty()) {
            consommationAnnuelle = estimerConsommationParDefaut(contrat);
        } else {
            consommationAnnuelle = calculerConsommationAnneePrecedente(contrat);
        }

        // 2️⃣ Tarif en vigueur
        Tarif tarif = gestionnaireTarifs.getTarifActif(dateEstimation);

        // 3️⃣ Calcul du coût de l'électricité via l'offre
        double coutElectriciteHT =
                contrat.getOffreTarifaire()
                        .calculerCoutElectricite(consommationAnnuelle, tarif);

        // 4️⃣ Abonnement annuel
        double abonnementAnnuelHT =
                contrat.getOffreTarifaire().calculerCoutAbonnementAnnuel(tarif) * 12;

        return coutElectriciteHT + abonnementAnnuelHT;
    }

    private Map<TypeConso, Double> estimerConsommationParDefaut(Contrat contrat) {

        double consoMoyenneAnnuelle = 4500;

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


    // TODO : METTRE EN PLACE LES CALCULS AVEC LES RELEVES
    private Map<TypeConso, Double> calculerConsommationAnneePrecedente(Contrat contrat) {
        // en utilisant les relevés N et N-1
    }


    public void cloturerContrat(Contrat contrat, LocalDate dateCloture) {

        if (!contrat.estActif()) {
            throw new IllegalStateException("Le contrat est déjà clôturé");
        }

        if (contrat.aDesFacturesImpayees()) {
            throw new IllegalStateException(
                    "Impossible de clôturer le contrat : factures impayées"
            );
        }

        // Fin de l’échéancier si présent
        if (contrat.getEcheancier() != null) {
            contrat.supprimerEcheancier();
        }

        contrat.cloturer(dateCloture);
    }



}
