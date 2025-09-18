package com.BIProject.Laborex.Service.SECURITE;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
@Component
public class JwtUtil {
	@Value("${jwt.secret}")
    private String secret;
	 // Clé secrète utilisée pour signer (chiffrer) et vérifier les JWT
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
 // Génère un token JWT avec un nom d'utilisateur et un identifiant de session (ssid)
    public String generateToken(UserDetails userDetails, String ssid) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("ssid", ssid)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 heure
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // Extrait le nom d'utilisateur (subject) du token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Récupère la valeur du champ "ssid" dans le token
    public String extractSsid(String token) {
        return extractAllClaims(token).get("ssid", String.class);
    }

    // Vérifie que le nom d'utilisateur dans le token correspond à l’utilisateur actuel
    // et que le token n’a pas expiré
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Vérifie si la date d’expiration du token est dépassée
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Méthode utilitaire pour parser et extraire toutes les claims du token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

}
