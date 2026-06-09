package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.Map;

/**
 * Implémentation de l'offre tarifaire classique.
 * <p>
 * L'offre classique repose sur un tarif unique du kWh,
 * sans distinction horaire. Toute la consommation est
 * comptabilisée sous le type {@link TypeConso#TOTAL}.
 * </p>
 */
public class OffreClassique extends OffreTarifaire {

    /**
     * Calcule le coût de l'électricité consommée selon l'offre classique.
     *
     * @param consommations consommations par type
     * @param tarif tarif en vigueur
     * @return coût total de l'électricité
     *
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws IllegalStateException si la consommation totale est absente
     */
    @Override
    public double calculerCoutElectricite(
            Map<TypeConso, Double> consommations,
            Tarif tarif
    ) {
        if (consommations == null || tarif == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        if (!consommations.containsKey(TypeConso.TOTAL)) {
            throw new IllegalStateException(
                    "Consommation TOTAL requise pour une offre classique"
            );
        }

        return consommations.get(TypeConso.TOTAL)
                * tarif.getPrixKwhClassique();
    }

    /**
     * Calcule le coût annuel de l'abonnement pour l'offre classique.
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
        return tarif.getPrixAbonnementClassique() * 12;
    }

    /**
     * Calcule le coût mensuel de l'abonnement pour l'offre classique.
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
        return tarif.getPrixAbonnementClassique();
    }

    /**
     * Calcule la consommation entre deux relevés pour l'offre classique.
     * <p>
     * Toute la consommation est regroupée sous le type {@code TOTAL}.
     * </p>
     *
     * @param precedent relevé précédent
     * @param courant relevé courant
     * @return consommation totale
     *
     * @throws IllegalArgumentException si les relevés sont invalides
     */
    @Override
    public Map<TypeConso, Double> calculerConsommation(
            Releve precedent,
            Releve courant
    ) {
        verifierReleves(precedent, courant);

        Map<TypeConso, Double> brute =
                courant.calculerConsommation(precedent);

        return Map.of(
                TypeConso.TOTAL,
                brute.get(TypeConso.TOTAL)
        );
    }

}
