package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.OffreTarifaire;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.facture.TauxTVA;
import fr.electronvert.facturation.model.facture.TypeFacture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GestionnaireFactures {

    private final GestionnaireTarifs gestionnaireTarifs;
    private final List<Facture> factures = new ArrayList<>();

    public GestionnaireFactures(GestionnaireTarifs gestionnaireTarifs) {
        if (gestionnaireTarifs == null) {
            throw new IllegalArgumentException("Le gestionnaire de tarifs est requis");
        }
        this.gestionnaireTarifs = gestionnaireTarifs;
    }


    // TODO : revoir les exceptions
    public Facture creerFactureMensuelle(
            Contrat contrat,
            LocalDate dateEmission
    ) {

        if (contrat == null  || dateEmission == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        if (!contrat.estActif()) {
            throw new IllegalStateException("Impossible de facturer un contrat inactif");
        }

        OffreTarifaire offreTarifaire = contrat.getOffreTarifaire();
        if (offreTarifaire == null) {
            throw new IllegalStateException("Aucune offre tarifaire associée au contrat");
        }


        LocalDate moisFacture = dateEmission.withDayOfMonth(1);
        if (factureExistePourMois(contrat, TypeFacture.MENSUELLE, moisFacture)) {
            throw new IllegalStateException(
                    "Une facture mensuelle existe déjà pour ce contrat et ce mois"
            );
        }
        // -------- Relevés --------
        Releve dernier = contrat.getDernierReleve();
        Releve precedent = contrat.getAvantDernierReleve();

        if (dernier == null || precedent == null) {
            throw new IllegalStateException("Il faut deux relevés pour facturer");
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

        // ---------- Frais de changement d’offre ----------
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
        return facture;
    }

    public void verifierEcheances(LocalDate date) {
        for (Facture facture : factures) {
            if (facture.getStatut() == StatutFacture.EMISE
                    && date.isAfter(facture.getDateEcheance())) {
                facture.passerEnImpayee(date);
            }
        }
    }

    public List<Facture> getFactures() {
        return Collections.unmodifiableList(factures);
    }

    public List<Facture> getFacturesImpayees() {
        List<Facture> resultat = new ArrayList<>();
        for (Facture f : factures) {
            if (f.getStatut() == StatutFacture.IMPAYEE) {
                resultat.add(f);
            }
        }
        return resultat;
    }

    public Facture rechercherParReference(String reference) {
        for (Facture f : factures) {
            if (f.getReference().equals(reference)) {
                return f;
            }
        }
        return null;
    }

    public List<Facture> getFacturesParClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être nul");
        }

        List<Facture> resultat = new ArrayList<>();
        for (Facture f : factures) {
            if(f.getContrat().getClient().equals(client)) {
                resultat.add(f);
            }
        }
        return resultat;
    }


    private boolean factureExistePourMois(
            Contrat contrat,
            TypeFacture type,
            LocalDate mois
    ) {
        for (Facture f : factures) {
            if (f.getContrat().equals(contrat)
                    && f.getType() == type
                    && f.getDateEmission().withDayOfMonth(1).equals(mois)) {
                return true;
            }
        }
        return false;
    }


// TODO : lire explication de comment marche le stream ici

    public String genererReferenceFacture(
            Contrat contrat,
            TypeFacture type,
            LocalDate dateEmission
    ) {
        String base = "FACT-%s-%s-%d-%02d"
                .formatted(
                        contrat.getId(),
                        type.name(),
                        dateEmission.getYear(),
                        dateEmission.getMonthValue()
                );

        long compteur = factures.stream()
                .filter(f -> f.getReference().startsWith(base))
                .count();

        return base + "-" + String.format("%03d", compteur + 1);
    }

}
