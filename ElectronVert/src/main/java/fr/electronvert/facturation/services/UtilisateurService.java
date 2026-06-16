package fr.electronvert.facturation.services;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.utilisateur.ClientResume;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.servlet.viewmodel.ClientAdminViewModel;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;
    private final ContratDAO contratDAO;

    public UtilisateurService(UtilisateurDAO utilisateurDAO,  ContratDAO contratDAO) {
        this.utilisateurDAO = utilisateurDAO;
        this.contratDAO = contratDAO;
    }

    public void modifierInfos(Utilisateur utilisateur, String prenom, String nom, String email) throws SQLException {
        utilisateur.setPrenom(prenom);
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateurDAO.update(utilisateur);
    }

    public void modifierMotDePasse(Utilisateur utilisateur, String mdpActuel, String nouveauMdp, String confirmationMdp) throws SQLException {
        if (!BCrypt.checkpw(mdpActuel, utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }
        if (!nouveauMdp.equals(confirmationMdp)) {
            throw new IllegalArgumentException("Les mots de passe saisis ne correspondent pas.");
        }
        String hash = BCrypt.hashpw(nouveauMdp, BCrypt.gensalt());
        utilisateur.setMotDePasse(hash);
        utilisateurDAO.updateMotDePasse(utilisateur.getId(), hash);
    }

    public int getNbNouveauxClients(YearMonth mois) throws SQLException{
        return utilisateurDAO.getNbNouveauClient(mois);
    }

    public List<ClientResume>  findDerniersClients(int nb) throws SQLException{
        return  utilisateurDAO.findDerniersClients(nb);
    }

    public List<ClientAdminViewModel> getClientsAdmin() throws SQLException{
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        List<Contrat> contratsActifs = contratDAO.findActifs();
        List<Utilisateur> clients = utilisateurs.stream().filter(u -> u.getRole() == RoleUtilisateur.CLIENT).toList();
        Map<Integer, List<Contrat>> contratsParClient = new HashMap<>();
        for(Contrat contrat : contratsActifs){
            contratsParClient.computeIfAbsent(contrat.getClient().getId(), id -> new ArrayList<>()).add(contrat);
        }
        List<ClientAdminViewModel> clientAdmins = new ArrayList<>();
        for(Utilisateur c : clients){
            List<Contrat> contrats = contratsParClient.getOrDefault(c.getId(), new ArrayList<>());
            List<String> offres = contrats.stream()
                    .map(contrat -> contrat.getOffreTarifaire() instanceof OffreClassique ? "Classique" : "HP/HC")
                    .distinct()
                    .toList();
            String dateInscription = contrats.stream()
                    .map(Contrat::getDateSouscription)
                    .min(LocalDate::compareTo)
                    .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .orElse("—");
            clientAdmins.add(new ClientAdminViewModel(c.getId(), c.getPrenom(), c.getNom(), c.getEmail(),
                    contrats.size(), offres, dateInscription));

        }
        return clientAdmins;

    }

    public List<ClientAdminViewModel> getClientAdminParEmail(String email) throws SQLException{
        Utilisateur c = utilisateurDAO.findByEmail(email);
        List<ClientAdminViewModel> clientAdmins = new ArrayList<>();
        if(c == null){
            return new ArrayList<>();
        }
        List<Contrat> contrats = contratDAO.findActifsByClientId(c.getId());
        List<String> offres = contrats.stream()
                .map(contrat -> contrat.getOffreTarifaire() instanceof OffreClassique ? "Classique" : "HP/HC")
                .distinct()
                .toList();
        String dateInscription = contrats.stream()
                .map(Contrat::getDateSouscription)
                .min(LocalDate::compareTo)
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .orElse("—");
        clientAdmins.add(new ClientAdminViewModel(c.getId(), c.getPrenom(), c.getNom(), c.getEmail(),
                contrats.size(), offres, dateInscription));
        return clientAdmins;
    }

}
