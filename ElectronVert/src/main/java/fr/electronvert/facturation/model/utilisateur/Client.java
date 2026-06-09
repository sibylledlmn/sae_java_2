package fr.electronvert.facturation.model.utilisateur;

public class Client extends Utilisateur {

    public Client(String id, String nom, String prenom, String email) {
        super(id, nom, prenom, email);
    }

    @Override
    public RoleUtilisateur getRole() {
        return RoleUtilisateur.CLIENT;
    }
}
