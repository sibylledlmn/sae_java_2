package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.Map;

public class OffreHPHC extends OffreTarifaire {
    @Override
    public Map<TypeConso, Double> calculerConsommation(Releve actuel, Releve precedent) {

        verifierReleves(actuel, precedent);

        double hp = actuel.getIndexHP() - precedent.getIndexHP();
        double hc = actuel.getIndexHC() - precedent.getIndexHC();

        if (hp < 0 || hc < 0) {
            throw new IllegalArgumentException("Index HP/HC incohérents");
        }

        return Map.of(
                TypeConso.HP, hp,
                TypeConso.HC, hc
        );
    }

    @Override
    public double calculerCoutElectricite(Map<TypeConso, Double> consommations, Tarif tarif) {
        return consommations.get(TypeConso.HP) * tarif.getPrixKwhHP()
                + consommations.get(TypeConso.HC) * tarif.getPrixKwhHC();    }

    @Override
    public double calculerCoutAbonnementAnnuel(Tarif tarif) {
        return tarif.getPrixAbonnementHPHC()*12;
    }

    @Override
    public double calculerCoutAbonnementMensuel(Tarif tarif) {
        return tarif.getPrixAbonnementHPHC();
    }

}


