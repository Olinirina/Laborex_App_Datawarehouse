package com.BIProject.Laborex;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.BIProject.Laborex.Entity.SECURITE.Role_Utilisateur;
import com.BIProject.Laborex.Entity.SECURITE.Utilisateur;
import com.BIProject.Laborex.Repository.SECURITE.UtilisateurRepository;

@SpringBootApplication
public class LaborexApplication {
	

	public static void main(String[] args) {
		SpringApplication.run(LaborexApplication.class, args);

	}
	/*@Bean
    CommandLineRunner init(UtilisateurRepository utilisateurRepository, PasswordEncoder encoder) {
        return args -> {
            if (utilisateurRepository.findByEmail("responsable@laborex.com").isEmpty()) {
                Utilisateur rp = new Utilisateur();
                rp.setEmail("responsableIT@laborex.com");
                rp.setMotDePasse(encoder.encode("laborexIT123")); // mot de passe hashé
                rp.setRole(Role_Utilisateur.RESPONSABLE_IT); // Responsable IT
                utilisateurRepository.save(rp);
                System.out.println("Responsable IT créé !");
            }
        };
    }*/

}
