package com.BIProject.Laborex.DAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
//Insérer de très grosses quantités de données rapidement et sans saturer la mémoire.
@Service
public class VenteBatchService {

	//Objet JPA: pour communiquer avec la BD
    @PersistenceContext
    private EntityManager entityManager;

    
    @Transactional//S'execute dans une transaction, si la transaction échoue => Tout est annulé
    public <T> void insertInBatch(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        //Traiter par paquets de 50 000 enregistrements
        int batchSize = 50000; 
        for (int i = 0; i < entities.size(); i++) {
            // Nouvelle Entite >> Inserer // Entité existante >> Mis à jour
            entityManager.merge(entities.get(i)); 
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush(); //Envoi toutes les operations SQL a la BD
                entityManager.clear();//vide le cache de l’EntityManager (sinon la mémoire explose)
            }
        }
        //Assurer que tout ce qui reste en mémoire est bien écrit en base
        entityManager.flush();
        entityManager.clear();
    }
}
