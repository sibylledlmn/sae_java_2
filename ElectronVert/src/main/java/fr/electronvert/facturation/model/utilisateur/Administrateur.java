package fr.electronvert.facturation.model.utilisateur;

//TODO : validation format, mettre adresse mail electronvert

public class Administrateur extends Utilisateur {

    public Administrateur(String id, String nom, String prenom, String email) {
        super(id, nom, prenom, email);
    }

    @Override
    public RoleUtilisateur getRole() {
        return RoleUtilisateur.ADMINISTRATEUR;
    }
}

