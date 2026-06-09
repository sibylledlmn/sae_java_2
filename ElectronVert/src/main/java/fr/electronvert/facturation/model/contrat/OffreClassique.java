package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.HashMap;
import java.util.Map;

public class OffreClassique extends  OffreTarifaire {




    @Override
    public Map<TypeConso, Double> calculerConsommation(Releve precedent, Releve actuel) {
        verifierReleves(precedent, actuel);
        double conso = actuel.getIndexTotal() - precedent.getIndexTotal();
        if (conso < 0) {
            throw new IllegalArgumentException("Index total incohérent");
        }

        return Map.of(TypeConso.TOTAL, conso);
    }


    @Override
    public double calculerCoutElectricite(Map<TypeConso, Double> consommations, Tarif tarif) {
        return consommations.get(TypeConso.TOTAL)
                * tarif.getPrixKwhClassique();    }

    @Override
    public double calculerCoutAbonnementAnnuel(Tarif tarif) {
        return tarif.getPrixAbonnementClassique()*12;
    }

    @Override
    public double calculerCoutAbonnementMensuel(Tarif tarif) {
        return tarif.getPrixAbonnementClassique();
    }



}
