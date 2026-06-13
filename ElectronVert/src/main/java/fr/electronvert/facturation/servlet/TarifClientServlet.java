package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.TarifDAO;
import fr.electronvert.facturation.dao.impl.TarifDAOJdbc;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import fr.electronvert.facturation.servlet.viewmodel.TarifViewModel;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/client/tarifs")
public class TarifClientServlet extends HttpServlet {

    TarifDAO tarifDAO = new TarifDAOJdbc();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateur == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        try {
            String dateParam = req.getParameter("date");
            if (dateParam != null && !dateParam.isEmpty()) {
                resp.setContentType("application/json;charset=UTF-8");
                Tarif tarif = tarifDAO.findActiveAtDate(LocalDate.parse(dateParam));
                if (tarif == null) {
                    resp.getWriter().write("{\"found\": false}");
                } else {
                    TarifViewModel vm = new TarifViewModel(tarif);
                    resp.getWriter().write("{\"found\": true, \"prixKwhClassique\": \"" + vm.getPrixKwhClassique() + "\", \"prixKwhHP\": \"" + vm.getPrixKwhHP() + "\", \"prixKwhHC\": \"" + vm.getPrixKwhHC() + "\", \"prixAbonnementClassique\": \"" + vm.getPrixAbonnementClassique() + "\", \"prixAbonnementHPHC\": \"" + vm.getPrixAbonnementHPHC() + "\"}");
                }
                return;
            }
            List<Tarif> tousLesTarifs = tarifDAO.findAll();
            List<TarifViewModel> tousLesTarifVM = new ArrayList<>();
            for (Tarif tarif : tousLesTarifs) {
                tousLesTarifVM.add(new TarifViewModel(tarif));
            }
            Map<String, Object> model = new HashMap<>();
            model.put("utilisateur", utilisateur);
            model.put("tarif", tousLesTarifVM.get(0));
            model.put("tarifs", tousLesTarifVM);
            model.put("pageActive", "tarifs");
            FreeMarkerUtil.render("tarifs-client.ftl", model, resp);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }


    }
}
