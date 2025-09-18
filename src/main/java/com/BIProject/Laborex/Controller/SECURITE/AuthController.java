package com.BIProject.Laborex.Controller.SECURITE;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.SECURITE.AuthRequest;
import com.BIProject.Laborex.Entity.SECURITE.Sessions;
import com.BIProject.Laborex.Entity.SECURITE.Utilisateur;
import com.BIProject.Laborex.Repository.SECURITE.SessionRepository;
import com.BIProject.Laborex.Repository.SECURITE.UtilisateurRepository;
import com.BIProject.Laborex.Service.SECURITE.CustomDetailsUtilisateurService;
import com.BIProject.Laborex.Service.SECURITE.JwtUtil;
import com.BIProject.Laborex.Service.SECURITE.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	//Pour authentifier l'utilisateur
	@Autowired private AuthenticationManager authManager;
	//Pour la deconnexion
	@Autowired private TokenBlacklistService tokenBlackListService;
	//Pour charger un utilisateur depuis la base de données
	@Autowired private CustomDetailsUtilisateurService userDetailsService;
	//Pour générer et lire les token JWT
	@Autowired private JwtUtil jwtUtil;
	
	@Autowired private UtilisateurRepository utilisateurRepository;
	@Autowired SessionRepository sessionRepository;
	
	//==========LOGIN
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request){
		//Verifier les identifiants
		authManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPass())
				);
		//On recupere l'utilisateur depuis la base si il existe sinon on lance une erreur
		Utilisateur utilisateur= utilisateurRepository.findByEmail(request.getUsername())
				.orElseThrow(()-> new UsernameNotFoundException("Utilisateur non trouvé"));
		
		//Creer un SSID unique (Identifiant de session) avec UUID
		String ssid= UUID.randomUUID().toString();
		
		//Creer une nouvelle session pour l'utilisateur
		Sessions session= new Sessions();
		session.setSsid(ssid);
		session.setCreatedAt(LocalDateTime.now());
		session.setUtilisateur(utilisateur);
		sessionRepository.save(session);
		
		//Recharge l'utilisateur pour construire le JWT
		UserDetails userDetails= userDetailsService.loadUserByUsername(request.getUsername());
		//Generer le token JWT avec le nom d'utilisateur et le ssid 
		String jwt= jwtUtil.generateToken(userDetails, ssid);
		
		//Preparer une reponse JSON avec le token et le ssid
		Map<String, String> response = new HashMap<>();
		response.put("jwt", jwt);
		response.put("ssid", ssid);
		
		//Renvoyer une reponse HTTP
		return ResponseEntity.ok(response);
	}
	
	//===============LOGOUT====================
	//Headers (Authorization , value= Bearer token....)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlackListService.blacklistToken(token);
            return ResponseEntity.ok("Déconnecté avec succès.");
        }

        return ResponseEntity.badRequest().body("Token invalide ou manquant.");
    }

}
