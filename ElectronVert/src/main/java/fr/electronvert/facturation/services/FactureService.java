package fr.electronvert.facturation.services;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.FraisRelanceDAO;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.FraisRelance;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.servlet.viewmodel.FactureImpayeeAdminViewModel;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FactureService {

   private final FactureDAO factureDAO ;
    private final FraisRelanceDAO fraisRelanceDAO;
    private final ContratDAO contratDAO;

    public FactureService(FactureDAO factureDAO, FraisRelanceDAO fraisRelanceDAO, ContratDAO contratDAO) {
        this.factureDAO = factureDAO;
        this.fraisRelanceDAO = fraisRelanceDAO;
        this.contratDAO = contratDAO;
    }

    public double getTotalMontantAPayerTTCAvecFrais(Facture facture) throws SQLException {
        return getTotauxFrais(facture).getTtc() + facture.getMontantTTC();
    }

    public TotauxFraisRelance getTotauxFrais(Facture facture) throws SQLException {
        List<FraisRelance> frais = fraisRelanceDAO.findByFactureId(facture.getId());
        double ht  = frais.stream().mapToDouble(FraisRelance::getMontantHT).sum();
        double tva = frais.stream().mapToDouble(FraisRelance::getMontantTVA).sum();
        double ttc = frais.stream().mapToDouble(FraisRelance::getMontantTTC).sum();
        return new TotauxFraisRelance(ht, tva, ttc);
    }

    public int getNbImpayées() throws SQLException {
        return factureDAO.findImpayees().size();
    }

    public double getMontantTotalImpayées() throws SQLException {
        double total = 0;
        for (Facture facture : factureDAO.findImpayees()) {
            total += facture.getMontantTTC();
        }
        return total;
    }

    public double getCAMensuel(YearMonth mois) throws SQLException {
        return factureDAO.getCAMensuel(mois);
    }

    public List<FactureImpayeeAdminViewModel> getFacturesImpayeesAdmin(int nbFactures) throws SQLException {
        List<Facture> impayees = factureDAO.findImpayees();
        // sublist -> ici 0 est l'index de départ, et pour Math.min(limite, impayees.size() ça veut dire que soit on prend une limite soit la taille de la liste si elle est plus petite
        List<Facture> limitees = impayees.subList(0, Math.min(nbFactures, impayees.size()));
        List<FactureImpayeeAdminViewModel> facturesImpayeeAdminVm = new ArrayList<>();
        for (Facture facture : limitees) {
            Utilisateur client = contratDAO.findById(facture.getContratId()).getClient();
            String nomClient = client.getPrenom() + " " + client.getNom();
            String reference = facture.getReference();
            String dateEcheance =   facture.getDateEcheance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String montantTTC = String.format("%.2f €", facture.getMontantTTC());
            int nbRelances = fraisRelanceDAO.findByFactureId(facture.getId()).size();
            FactureImpayeeAdminViewModel factureImpayeeVmAdmin = new FactureImpayeeAdminViewModel(nomClient, reference, dateEcheance, montantTTC, nbRelances);
            facturesImpayeeAdminVm.add(factureImpayeeVmAdmin);
        }
        return facturesImpayeeAdminVm;
    }



}
