package com.BIProject.Laborex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.Labo;



@Repository
public interface LaboRepository extends JpaRepository<Labo, String> {

}
