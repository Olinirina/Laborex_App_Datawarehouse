package com.BIProject.Laborex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.Vente;



@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {
	

}
