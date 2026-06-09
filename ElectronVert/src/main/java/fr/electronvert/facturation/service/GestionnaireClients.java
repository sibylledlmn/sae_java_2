package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.utilisateur.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionnaireClients {

    private final List<Client> clients = new ArrayList<>();

    public void ajouterClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        if (rechercherParEmail(client.getEmail()) != null) {
            throw new IllegalArgumentException(
                    "Un client avec l'email " + client.getEmail() + " existe déjà"
            );
        }

        clients.add(client);
    }

    public Client rechercherParEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être null ou vide");
        }

        for (Client c : clients) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }

    public List<Client> getTousLesClients() {
        return Collections.unmodifiableList(clients);
    }

    public int getNombreClients() {
        return clients.size();
    }
}
