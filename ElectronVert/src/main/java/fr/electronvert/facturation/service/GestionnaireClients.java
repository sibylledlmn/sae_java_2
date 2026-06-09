package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.ClientDejaExistantException;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service de gestion des clients.
 * <p>
 * Cette classe est responsable de :
 * <ul>
 *     <li>la création des clients</li>
 *     <li>la recherche de clients</li>
 *     <li>la fourniture de statistiques globales</li>
 * </ul>
 *
 */
public class GestionnaireClients {

    /**
     * Liste interne des clients enregistrés.
     */
    private final List<Client> clients = new ArrayList<>();

    // =====================
    // CRÉATION
    // =====================

    /**
     * Crée et enregistre un nouveau client.
     *
     * @param nom nom du client
     * @param prenom prénom du client
     * @param email adresse email du client
     * @return client créé
     *
     * @throws ClientDejaExistantException si un client avec le même email existe déjà
     */
    public Client creerClient(String nom, String prenom, String email) {
        Client existant = rechercherParEmail(email);
        if (existant != null) {
            throw new ClientDejaExistantException(
                    existant.getEmail(),
                    existant.getId()
            );
        }

        Client client = new Client(nom, prenom, email);
        clients.add(client);
        return client;
    }

    // =====================
    // RECHERCHE
    // =====================

    /**
     * Recherche un client à partir de son adresse email.
     *
     * @param email email du client
     * @return client correspondant ou {@code null} s'il n'existe pas
     *
     * @throws IllegalArgumentException si l'email est null ou vide
     */
    public Client rechercherParEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "L'email ne peut pas être null ou vide"
            );
        }

        for (Client c : clients) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }

    // =====================
    // ACCÈS AUX DONNÉES
    // =====================

    /**
     * Retourne la liste de tous les clients enregistrés.
     *
     * @return liste non modifiable des clients
     */
    public List<Client> getTousLesClients() {
        return Collections.unmodifiableList(clients);
    }

    // =====================
    // STATISTIQUES
    // =====================

    /**
     * Retourne le nombre total de clients enregistrés.
     *
     * @return nombre de clients
     */
    public int getNombreClients() {
        return clients.size();
    }

    /**
     * Retourne le nombre de clients ayant au moins un contrat actif.
     *
     * @return nombre de clients actifs
     */
    public long getNombreClientsActifs() {
        return clients.stream()
                .filter(Client::aUnContratActif)
                .count();
    }
}
