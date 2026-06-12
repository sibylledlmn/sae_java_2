package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.ReleveDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.ReleveDAOJdbc;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.OffreHPHC;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import fr.electronvert.facturation.servlet.viewmodel.ContratConsommationViewModel;
import fr.electronvert.facturation.servlet.viewmodel.ReleveViewModel;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/client/consommation")
public class ReleveClientServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOJdbc();
    private final ReleveDAO releveDAO = new ReleveDAOJdbc();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateur == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
            List<Contrat> tousContrats = contratDAO.findByClientId(utilisateur.getId());
            List<ContratConsommationViewModel> contrats = new ArrayList<>();

            for (Contrat c : tousContrats) {
                List<Releve> tousReleves = releveDAO.findByContractId(c.getId());
                List<ReleveViewModel> releves = new ArrayList<>();
                boolean hphc = c.getOffreTarifaire() instanceof OffreHPHC;

                for (int i = 0; i < tousReleves.size(); i++) {
                    Releve r = tousReleves.get(i);
                    String date = r.getDateDeReleve().format(fmt);
                    String typeLibelle = libelleTypeReleve(r.getTypeReleve());

                    if (!hphc) {
                        String indexTotal = String.format("%.0f", r.getIndex().get(TypeConso.TOTAL));
                        String conso;
                        if (i == tousReleves.size() - 1) {
                            conso = "—";
                        } else {
                            Map<TypeConso, Double> diff = r.calculerConsommation(tousReleves.get(i + 1));
                            conso = String.format("%.0f kWh", diff.get(TypeConso.TOTAL));
                        }
                        releves.add(new ReleveViewModel(date, typeLibelle, indexTotal, conso));
                    } else {
                        String indexHP = String.format("%.0f", r.getIndex().get(TypeConso.HP));
                        String indexHC = String.format("%.0f", r.getIndex().get(TypeConso.HC));
                        String consoHP;
                        String consoHC;
                        if (i == tousReleves.size() - 1) {
                            consoHP = "—";
                            consoHC = "—";
                        } else {
                            Map<TypeConso, Double> diff = r.calculerConsommation(tousReleves.get(i + 1));
                            consoHP = String.format("%.0f kWh", diff.get(TypeConso.HP));
                            consoHC = String.format("%.0f kWh", diff.get(TypeConso.HC));
                        }
                        releves.add(new ReleveViewModel(date, typeLibelle, indexHP, indexHC, consoHP, consoHC));
                    }
                }

                String dernierReleve = tousReleves.isEmpty()
                        ? "Aucun relevé"
                        : tousReleves.get(0).getDateDeReleve().format(fmt);

                contrats.add(new ContratConsommationViewModel(
                        c.getId(),
                        c.getAdressePostale(),
                        c.getLibelleOffreTarifaire(c.getOffreTarifaire()),
                        c.getLibelleModeFacturation(c.getModeFacturation()),
                        c.getDateSouscription().format(fmt),
                        dernierReleve,
                        hphc,
                        releves
                ));
            }

            Map<String, Object> model = new HashMap<>();
            model.put("utilisateur", utilisateur);
            model.put("contrats", contrats);
            model.put("pageActive", "consommation");
            FreeMarkerUtil.render("ma-consommation.ftl", model, resp);

        } catch (SQLException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private String libelleTypeReleve(TypeReleve type) {
        return switch (type) {
            case MENSUEL -> "Périodique";
            case OUVERTURE -> "Ouverture";
            case CLOTURE -> "Clôture";
            case REGULARISATION -> "Régularisation";
        };
    }
}
