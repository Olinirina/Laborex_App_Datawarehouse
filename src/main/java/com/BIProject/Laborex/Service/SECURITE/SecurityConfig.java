package com.BIProject.Laborex.Service.SECURITE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Composant configuré
@EnableWebSecurity // Activer Spring Security
@EnableMethodSecurity // ← nécessaire pour que @PreAuthorize marche
public class SecurityConfig {

    @Autowired 
    private CustomDetailsUtilisateurService userDetailsService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    //Hierarchie des roles (RESPONSABLE_IT est l'administrateur, il aura accès à tous les fonctionnalités)
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(
            "ROLE_RESPONSABLE_IT > ROLE_DG \n" +
            "ROLE_RESPONSABLE_IT > ROLE_COMMERCIAL \n" +
            "ROLE_RESPONSABLE_IT > ROLE_TRANSIT"
        );
        return hierarchy;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests()
         // Swagger endpoints (doc & UI) → accessibles sans auth
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml").permitAll()
            .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Ici on ajoute le filtre JWT
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
