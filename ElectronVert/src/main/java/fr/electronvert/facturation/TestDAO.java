package fr.electronvert.facturation;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.ReleveDAO;
import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.FactureDAOJdbc;
import fr.electronvert.facturation.dao.impl.ReleveDAOJdbc;
import fr.electronvert.facturation.dao.impl.UtilisateurDAOJdbc;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.TypeFacture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class
TestDAO {

    public static void main(String[] args) {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAOJdbc();
        ContratDAO contratDAO = new ContratDAOJdbc();
        ReleveDAO releveDAO = new ReleveDAOJdbc();
        FactureDAO factureDAO = new FactureDAOJdbc();

        try {
            // =====================
            // TEST UTILISATEUR
            // =====================
            System.out.println("=== TEST UTILISATEUR ===");

            Utilisateur client = new Utilisateur("Dupont", "Jean", "jean.duport@test.com", RoleUtilisateur.CLIENT);
            client.setMotDePasse("test123");
            int clientId = utilisateurDAO.save(client);
            client.setId(clientId);
            System.out.println("Utilisateur créé avec id=" + clientId);

            Utilisateur clientRecupere = utilisateurDAO.findById(clientId);
            System.out.println("Utilisateur récupéré : " + clientRecupere.getPrenom() + " " + clientRecupere.getNom());

            Utilisateur clientParEmail = utilisateurDAO.findByEmail("jean.dupont@test.com");
            System.out.println("Utilisateur par email : " + clientParEmail.getPrenom() + " " + clientParEmail.getNom());

            // =====================
            // TEST CONTRAT
            // =====================
            System.out.println("\n=== TEST CONTRAT ===");

            Contrat contrat = new Contrat(client, "12 rue de la Paix, 75001 Paris",
                    new OffreClassique(), ModeFacturation.REEL, LocalDate.now());
            int contratId = contratDAO.save(contrat);
            contrat.setId(contratId);
            System.out.println("Contrat créé avec id=" + contratId);

            Contrat contratRecupere = contratDAO.findById(contratId);
            System.out.println("Contrat récupéré : id=" + contratRecupere.getId()
                    + " | offre=" + contratRecupere.getOffreTarifaire().getClass().getSimpleName()
                    + " | statut=" + contratRecupere.getStatut());

            List<Contrat> contratsDuClient = contratDAO.findByClientId(clientId);
            System.out.println("Contrats du client : " + contratsDuClient.size());

            // =====================
            // TEST RELEVE
            // =====================
            System.out.println("\n=== TEST RELEVE ===");

            Releve releve = new Releve(contrat, TypeReleve.OUVERTURE,
                    LocalDate.now(), Map.of(TypeConso.TOTAL, 1000.0));
            int releveId = releveDAO.save(releve, contratId);
            releve.setId(releveId);
            System.out.println("Relevé créé avec id=" + releveId);

            List<Releve> releves = releveDAO.findByContractId(contratId);
            System.out.println("Relevés du contrat : " + releves.size());
            System.out.println("Dernier relevé : " + releveDAO.findDernerByContractId(contratId).toResume());

            // =====================
            // TEST FACTURE
            // =====================
            System.out.println("\n=== TEST FACTURE ===");

            Facture facture = new Facture(contrat, LocalDate.now(),
                    "FAC-TEST-001", TypeFacture.MENSUELLE);
            facture.definirMontants(100.0, 20.0, 120.0);
            int factureId = factureDAO.save(facture, contratId);
            facture.setId(factureId);
            System.out.println("Facture créée avec id=" + factureId);

            Facture factureRecuperee = factureDAO.findById(factureId);
            System.out.println("Facture récupérée : " + factureRecuperee.toResume());

            List<Facture> facturesDuContrat = factureDAO.findByContratId(contratId);
            System.out.println("Factures du contrat : " + facturesDuContrat.size());
//
//            // =====================
//            // NETTOYAGE
//            // =====================
//            System.out.println("\n=== NETTOYAGE ===");
//            utilisateurDAO.delete(clientId);
//            System.out.println("Données de test supprimées (CASCADE sur contrat, releve, facture)");
//
//            System.out.println("\nTous les tests sont passés !");

        } catch (Exception e) {
            System.err.println("ERREUR : " + e.getMessage());
            e.printStackTrace();
        }
    }
}