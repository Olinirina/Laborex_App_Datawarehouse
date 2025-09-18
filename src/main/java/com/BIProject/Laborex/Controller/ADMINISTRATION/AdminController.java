package com.BIProject.Laborex.Controller.ADMINISTRATION;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.SECURITE.Sessions;
import com.BIProject.Laborex.Entity.SECURITE.Utilisateur;
import com.BIProject.Laborex.Repository.SECURITE.SessionRepository;
import com.BIProject.Laborex.Repository.SECURITE.UtilisateurRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('RESPONSABLE_IT')")// tout ce controller est réservé au responsable IT
public class AdminController {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private SessionRepository sessionRepository;

    //========== AJOUTER UN UTILISATEUR ==========
    @PostMapping("/utilisateurs")
    public ResponseEntity<?> addUtilisateur(@RequestBody Utilisateur utilisateur) {
        // Vérifier que l'email est unique
        if(utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé !");
        }
        // Hasher le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok("Utilisateur ajouté avec succès !");
    }

    //========== MODIFIER UN UTILISATEUR ==========
    @PutMapping("/utilisateurs/{id}")
    public ResponseEntity<?> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur updatedUser) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setEmail(updatedUser.getEmail());
        if(updatedUser.getMotDePasse() != null && !updatedUser.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(updatedUser.getMotDePasse()));
        }
        utilisateur.setRole(updatedUser.getRole());
        utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok("Utilisateur mis à jour !");
    }

    //========== SUPPRIMER UN UTILISATEUR ==========
    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok("Utilisateur supprimé !");
    }

    //========== LISTER TOUS LES UTILISATEURS ==========
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> listUtilisateurs() {
        return ResponseEntity.ok(utilisateurRepository.findAll());
    }

    //========== LISTER LES SESSIONS ==========
    @GetMapping("/sessions")
    public ResponseEntity<List<Sessions>> listSessions() {
        return ResponseEntity.ok(sessionRepository.findAll());
    }

    //========== VERIFIER UNE SESSION ==========
    @GetMapping("/sessions/{ssid}")
    public ResponseEntity<?> getSession(@PathVariable String ssid) {
        Sessions session = sessionRepository.findBySsid(ssid)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
        return ResponseEntity.ok(session);
    }
}
