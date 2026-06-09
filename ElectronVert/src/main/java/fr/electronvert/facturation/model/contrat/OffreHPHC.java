package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.Map;

/**
 * Implémentation de l'offre tarifaire Heures Pleines / Heures Creuses (HP/HC).
 * <p>
 * Cette offre distingue deux types de consommation :
 * <ul>
 *     <li>Heures Pleines (HP)</li>
 *     <li>Heures Creuses (HC)</li>
 * </ul>
 * Chacune est facturée selon un tarif spécifique.
 *
 */
public class OffreHPHC extends OffreTarifaire {

    /**
     * Calcule le coût de l'électricité consommée selon l'offre HP/HC.
     *
     * @param consommations consommations par type (HP et HC)
     * @param tarif tarif en vigueur
     * @return coût total de l'électricité
     *
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws IllegalStateException si les consommations HP ou HC sont absentes
     */
    @Override
    public double calculerCoutElectricite(
            Map<TypeConso, Double> consommations,
            Tarif tarif
    ) {
        if (consommations == null || tarif == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!consommations.containsKey(TypeConso.HP)
                || !consommations.containsKey(TypeConso.HC)) {
            throw new IllegalStateException(
                    "Consommations HP/HC manquantes pour une offre HP/HC"
            );
        }

        double hp = consommations.get(TypeConso.HP);
        double hc = consommations.get(TypeConso.HC);

        return hp * tarif.getPrixKwhHP()
                + hc * tarif.getPrixKwhHC();
    }

    /**
     * Calcule le coût annuel de l'abonnement pour l'offre HP/HC.
     *
     * @param tarif tarif en vigueur
     * @return coût annuel de l'abonnement
     *
     * @throws IllegalArgumentException si le tarif est invalide
     */
    @Override
    public double calculerCoutAbonnementAnnuel(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Tarif invalide");
        }
        return tarif.getPrixAbonnementHPHC() * 12;
    }

    /**
     * Calcule le coût mensuel de l'abonnement pour l'offre HP/HC.
     *
     * @param tarif tarif en vigueur
     * @return coût mensuel de l'abonnement
     *
     * @throws IllegalArgumentException si le tarif est invalide
     */
    @Override
    public double calculerCoutAbonnementMensuel(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Tarif invalide");
        }
        return tarif.getPrixAbonnementHPHC();
    }

    /**
     * Calcule la consommation entre deux relevés pour l'offre HP/HC.
     * <p>
     * Les relevés sont vérifiés avant le calcul afin de garantir
     * leur cohérence chronologique.
     * </p>
     *
     * @param precedent relevé précédent
     * @param actuel relevé actuel
     * @return consommation par type (HP et HC)
     *
     * @throws IllegalArgumentException si les relevés sont invalides
     */
    @Override
    public Map<TypeConso, Double> calculerConsommation(
            Releve precedent,
            Releve actuel
    ) {
        verifierReleves(precedent, actuel);

        Map<TypeConso, Double> brute =
                actuel.calculerConsommation(precedent);

        return Map.of(
                TypeConso.HP, brute.get(TypeConso.HP),
                TypeConso.HC, brute.get(TypeConso.HC)
        );
    }
}




