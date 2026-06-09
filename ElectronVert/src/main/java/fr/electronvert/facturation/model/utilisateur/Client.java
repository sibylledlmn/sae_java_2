package fr.electronvert.facturation.model.utilisateur;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.util.ValidationFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente un client du système ElectronVert.
 * <p>
 * Un client est un utilisateur pouvant posséder un ou plusieurs contrats
 * d'électricité. Il peut être actif ou inactif en fonction de l'état
 * de ses contrats.
 * </p>
 */
public class Client extends Utilisateur {

    /**
     * Compteur utilisé pour générer des identifiants uniques
     * pour les clients.
     */
    private static int compteur = 1;

    /**
     * Liste des contrats associés au client.
     */
    private final List<Contrat> contrats = new ArrayList<>();

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un client avec les informations fournies.
     * <p>
     * L'identifiant du client est généré automatiquement.
     * </p>
     *
     * @param nom nom du client
     * @param prenom prénom du client
     * @param email adresse email du client
     *
     * @throws IllegalArgumentException si les informations sont invalides
     */
    public Client(String nom, String prenom, String email) {
        super(genererId(), nom, prenom, email);
    }

    // =====================
    // MÉTHODES STATIQUES
    // =====================

    /**
     * Génère un identifiant unique pour un client.
     *
     * @return identifiant sous la forme {@code CLI-x}
     */
    private static String genererId() {
        return "CLI-" + compteur++;
    }

    // =====================
    // MÉTHODES MÉTIER
    // =====================

    /**
     * Ajoute un contrat à la liste des contrats du client.
     *
     * @param contrat contrat à ajouter
     */
    public void ajouterContrat(Contrat contrat) {
        contrats.add(contrat);
    }

    /**
     * Indique si le client possède au moins un contrat actif.
     *
     * @return {@code true} si au moins un contrat est actif,
     *         {@code false} sinon
     */
    public boolean aUnContratActif() {
        return contrats.stream().anyMatch(Contrat::estActif);
    }

    /**
     * Retourne le rôle de l'utilisateur.
     *
     * @return {@link RoleUtilisateur#CLIENT}
     */
    @Override
    public RoleUtilisateur getRole() {
        return RoleUtilisateur.CLIENT;
    }

    // =====================
    // GETTERS
    // =====================

    /**
     * Retourne la liste des contrats du client.
     * <p>
     * La liste retournée est non modifiable afin de préserver
     * l'encapsulation.
     * </p>
     *
     * @return liste non modifiable des contrats
     */
    public List<Contrat> getContrats() {
        return Collections.unmodifiableList(contrats);
    }

    // =====================
    // SETTERS
    // =====================

    /**
     * Modifie le prénom du client.
     *
     * @param prenom nouveau prénom
     *
     * @throws IllegalArgumentException si le prénom est vide ou invalide
     */
    public void setPrenom(String prenom) {
        ValidationFormat.verifierNonVide(prenom, "Prénom");
        this.prenom = prenom;
    }

    /**
     * Modifie le nom du client.
     *
     * @param nom nouveau nom
     *
     * @throws IllegalArgumentException si le nom est vide ou invalide
     */
    public void setNom(String nom) {
        ValidationFormat.verifierNonVide(nom, "Nom");
        this.nom = nom;
    }

    /**
     * Modifie l'adresse email du client.
     *
     * @param email nouvelle adresse email
     *
     * @throws IllegalArgumentException si l'email est invalide
     */
    public void setEmail(String email) {
        ValidationFormat.verifierNonVide(email, "Email");
        this.email = email;
    }

    // =====================
    // MÉTHODES UTILITAIRES
    // =====================

    /**
     * Retourne une représentation textuelle du client,
     * utilisée pour l'affichage dans les vues.
     *
     * @return description du client
     */
    public String getInformationsPersonnelles() {
        return "ID : " + id + "\n"
                + "Nom : " + prenom + " " + nom + "\n"
                + "Email : " + email;
    }

}


