package fr.electronvert.facturation.services;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.exception.ChangementModeFacturationImpossibleException;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreTarifaire;

import java.sql.SQLException;
import java.time.LocalDate;

public class ContratService {

    private final ContratDAO contratDAO;
    private final FactureDAO factureDAO;


    public ContratService(ContratDAO contratDAO, FactureDAO factureDAO) {
        this.contratDAO = contratDAO;
        this.factureDAO = factureDAO;
    }

    public boolean changerOffre(int contratId, OffreTarifaire nouvelleOffre) throws SQLException {
        Contrat contrat = contratDAO.findById(contratId);
       if(contrat == null)  throw new IllegalArgumentException("Contrat introuvable : " + contratId);
       boolean frais = !contrat.changementOffreGratuit(LocalDate.now());
       if(frais == true){contrat.ajouterFraisChangementOffre(OffreTarifaire.getFraisChangementOffreHT());}
       contrat.planifierChangementOffreTarifaire(nouvelleOffre);
       contratDAO.update(contrat);
       return frais;
    }

    public void changerModeFacturation(int contratId, ModeFacturation nouveauMode) throws SQLException {
        Contrat contrat = contratDAO.findById(contratId);
        if(contrat == null)  throw new IllegalArgumentException("Contrat introuvable : " + contratId);
        boolean possible = contrat.changementModeFacturationPossible(LocalDate.now());
        if (!possible) {
            LocalDate prochaineDate = contrat.calculerProchaineDateChangementMode(LocalDate.now());
            throw new ChangementModeFacturationImpossibleException(
                    String.valueOf(contratId),
                    contrat.getModeFacturation(),
                    nouveauMode,
                    prochaineDate
            );
        }
        contrat.planifierChangementModeFacturation(nouveauMode);
        contratDAO.update(contrat);
    }


}
