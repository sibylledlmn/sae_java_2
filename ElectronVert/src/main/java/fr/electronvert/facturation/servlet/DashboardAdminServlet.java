package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.FraisRelanceDAO;
import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.FactureDAOJdbc;
import fr.electronvert.facturation.dao.impl.FraisRelanceDAOJdbc;
import fr.electronvert.facturation.dao.impl.UtilisateurDAOJdbc;
import fr.electronvert.facturation.model.utilisateur.ClientResume;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.services.ContratService;
import fr.electronvert.facturation.services.FactureService;
import fr.electronvert.facturation.services.UtilisateurService;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import fr.electronvert.facturation.servlet.viewmodel.FactureImpayeeAdminViewModel;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class DashboardAdminServlet extends HttpServlet {

    private final FactureDAO factureDAO = new FactureDAOJdbc();
    private final ContratDAO contratDAO = new ContratDAOJdbc();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAOJdbc();
    private final FraisRelanceDAO fraisRelanceDAO = new FraisRelanceDAOJdbc();
    private final int nbFacturesImpayeesAAfficher = 5;
    private final int nbDerniersClientssAAfficher = 3;


//    AJOUTER SECURITE GENRE ETRE SUR QUE USER ETS BIEN AMIN ?

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateur == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        if (utilisateur.getRole() != RoleUtilisateur.ADMINISTRATEUR) {
            resp.sendRedirect(req.getContextPath() + "/client/dashboard");
            return;
        }
        ContratService contratService = new ContratService(contratDAO, factureDAO);
        FactureService factureService = new FactureService(factureDAO, fraisRelanceDAO, contratDAO);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurDAO);
        YearMonth moisCourant = YearMonth.now();
        String mois = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
        mois = mois.substring(0, 1).toUpperCase() + mois.substring(1);
        try {
            int nbClientsActifs = contratService.getNbClientsActifs();
            int nouveauxClientsMois = utilisateurService.getNbNouveauxClients(moisCourant);
            int nbContratsActifs = contratService.getNbContratsActifs();
            int nbContratsClotures =  contratService.getNbContratsClotures();
            double caMensuel = factureService.getCAMensuel(moisCourant);
            String caMensuelString = String.format("%.2f €", caMensuel);
            double caMoisPrecent = factureService.getCAMensuel(moisCourant.minusMonths(1));
            double variation =  caMoisPrecent == 0 ? 0 : ((caMensuel - caMoisPrecent) / caMoisPrecent) * 100;
            String variationCA = String.format("%+.0f%% vs mois dernier", variation);
            int nbImpayees = factureService.getNbImpayées();
            String totalImpayees = String.format("%.2f €", factureService.getMontantTotalImpayées());
            List<FactureImpayeeAdminViewModel> facturesImpayees= factureService.getFacturesImpayeesAdmin(nbFacturesImpayeesAAfficher);
            int pourcentageClassique =   (int) contratService.getPourcentageOffreClassique();
            int pourcentageHphc = (int) contratService.getPourcentageOffreHPHC();
            int pourcentageReel = (int) contratService.getPourcentageModeFacturationReel();
            int pourcentageEcheancier = (int) contratService.getPourcentageModeFacturationEcheancier();
            List<ClientResume> derniersClients = utilisateurService.findDerniersClients(nbDerniersClientssAAfficher);



            Map<String, Object> model = new HashMap<>();
            model.put("mois", mois);
            model.put("nbClientsActif", nbClientsActifs  );
            model.put("nouveauxClientsMois", nouveauxClientsMois);
            model.put("nbContratsActifs", nbContratsActifs);
            model.put("nbContratsClotures", nbContratsClotures );
            model.put("caMensuel", caMensuelString );
            model.put("variationCA", variationCA );
            model.put("nbImpayee", nbImpayees );
            model.put("totalImpayee", totalImpayees);
            model.put("facturesImpayees", facturesImpayees);
            model.put("pctClassique", pourcentageClassique );
            model.put("pctHphc",pourcentageHphc );
            model.put("pctReel",pourcentageReel );
            model.put("pctEcheancier", pourcentageEcheancier);
            model.put("derniersClients", derniersClients );
            model.put("administrateur", utilisateur );
            model.put("pageActive", "dashboard" );
            FreeMarkerUtil.render("dashboard-admin.ftl", model, resp);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }


    }
}
