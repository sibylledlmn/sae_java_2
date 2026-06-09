package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class SimulateurIndex {

    private static final double INDEX_MIN = 1_000;
    private static final double INDEX_MAX = 10_000;

    /**
     * Génère des index initiaux non nuls pour un contrat
     * (simulation d’un compteur déjà existant)
     */
    public Map<TypeConso, Double> genererIndexInitial(Contrat contrat) {
        Map<TypeConso, Double> index = new HashMap<>();

        for (TypeConso type : TypeConso.values()) {
            double valeur = INDEX_MIN
                    + Math.random() * (INDEX_MAX - INDEX_MIN);
            index.put(type, valeur);
        }

        return index;
    }

    private long nombreDeJoursDepuisDernierReleve(
            Contrat contrat,
            LocalDate date
    ) {
        Releve dernier = contrat.getDernierReleve();
        return ChronoUnit.DAYS.between(
                dernier.getDateDeReleve(),
                date
        );
    }

    /**
     * Calcule les nouveaux index à partir du dernier relevé,
     * en tenant compte de la saison et de nombres de jours passés depuis le dernier relevé
     */
    public Map<TypeConso, Double> calculerIndex(
            Contrat contrat,
            LocalDate date
    ) {
        Releve dernier = contrat.getDernierReleve();
        Map<TypeConso, Double> anciensIndex = dernier.getIndex();

        long jours = nombreDeJoursDepuisDernierReleve(contrat, date);
        double coefSaison = coefficientSaison(date);

        Map<TypeConso, Double> nouveauxIndex = new HashMap<>();

        for (TypeConso type : anciensIndex.keySet()) {
            double consoMensuelle = consommationDeBase(type);
            double consoJournaliere = consoMensuelle / 30.0;

            double consommation =
                    consoJournaliere * jours * coefSaison;

            nouveauxIndex.put(
                    type,
                    anciensIndex.get(type) + consommation
            );
        }

        return nouveauxIndex;
    }

    /**
     * Consommation moyenne de base selon le type
     */
    private double consommationDeBase(TypeConso type) {
        switch (type) {
            case HP: return 120;
            case HC: return 80;
            default: return 100;
        }
    }

    /**
     * Coefficient multiplicateur selon la saison
     */
    private double coefficientSaison(LocalDate date) {
        int mois = date.getMonthValue();

        if (mois == 12 || mois == 1 || mois == 2) {
            return 1.5; // hiver
        }
        if (mois >= 6 && mois <= 8) {
            return 0.8; // été
        }
        return 1.0; // printemps / automne
    }
}
