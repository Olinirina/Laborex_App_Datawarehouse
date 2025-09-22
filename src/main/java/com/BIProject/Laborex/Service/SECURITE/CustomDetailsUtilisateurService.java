package com.BIProject.Laborex.Service.SECURITE;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.BIProject.Laborex.Entity.SECURITE.Utilisateur;
import com.BIProject.Laborex.Repository.SECURITE.UtilisateurRepository;

@Service
public class CustomDetailsUtilisateurService implements UserDetailsService {
    @Autowired 
    public UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur= utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return new User(
        	    utilisateur.getEmail(),
        	    utilisateur.getMotDePasse(),
        	    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole()))
        	);

    }
}

