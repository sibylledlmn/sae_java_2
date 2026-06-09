package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.DemandeModificationContrat;

import java.util.ArrayList;
import java.util.List;

public class GestionnaireDemandesModificationContrat {

    private final List<DemandeModificationContrat> demandes = new ArrayList<>();

    public void ajouterDemande(DemandeModificationContrat demande) {
        if (demande == null) {
            throw new IllegalArgumentException("Demande invalide");
        }
        demandes.add(demande);
    }

    public List<DemandeModificationContrat> getDemandes() {
        return List.copyOf(demandes);
    }


}
