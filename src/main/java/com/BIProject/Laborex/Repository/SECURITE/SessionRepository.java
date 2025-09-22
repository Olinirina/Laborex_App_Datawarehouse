package com.BIProject.Laborex.Repository.SECURITE;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.SECURITE.Sessions;
import com.BIProject.Laborex.Entity.SECURITE.Utilisateur;

@Repository
public interface SessionRepository extends JpaRepository<Sessions, Long> {
    List<Sessions> findByUtilisateur(Utilisateur utilisateur);
    Optional<Sessions> findBySsid(String ssid);

}
