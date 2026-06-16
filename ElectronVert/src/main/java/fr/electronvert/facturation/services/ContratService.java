package fr.electronvert.facturation.services;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.exception.ChangementModeFacturationImpossibleException;
import fr.electronvert.facturation.model.contrat.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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

    public int getNbContratsActifs() throws SQLException {
        List<Contrat> contratsActifs = contratDAO.findActifs();
        return contratsActifs.size();
    }

    public int getNbContratsClotures() throws SQLException {
        return contratDAO.nbContratsClotures();
    }

    public int getNbClientsActifs() throws SQLException {
        return (int) contratDAO.findActifs().stream()
                .map(c -> c.getClient().getId())
                .distinct()
                .count();
    }

    public double getPourcentageOffreClassique() throws SQLException {
        return getPourcentageOffre(contratDAO.findActifs(), OffreClassique.class);
    }

    public double getPourcentageOffreHPHC() throws SQLException {
        return getPourcentageOffre(contratDAO.findActifs(), OffreHPHC.class);
    }

    public double getPourcentageModeFacturationReel() throws SQLException {
        return getPourcentageMode(contratDAO.findActifs(), ModeFacturation.REEL);
    }

    public double getPourcentageModeFacturationEcheancier() throws SQLException {
        return getPourcentageMode(contratDAO.findActifs(), ModeFacturation.ECHEANCIER);
    }

    private double getPourcentageOffre(List<Contrat> contrats, Class<? extends OffreTarifaire> type) {
        if (contrats.isEmpty()) return 0;
        long nb = contrats.stream().filter(c -> type.isInstance(c.getOffreTarifaire())).count();
        return (nb / (double) contrats.size()) * 100;
    }

    private double getPourcentageMode(List<Contrat> contrats, ModeFacturation mode) {
        if (contrats.isEmpty()) return 0;
        long nb = contrats.stream().filter(c -> c.getModeFacturation() == mode).count();
        return (nb / (double) contrats.size()) * 100;
    }






}
