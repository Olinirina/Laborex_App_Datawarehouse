package com.BIProject.Laborex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.Tva;


@Repository
public interface TvaRepository extends JpaRepository<Tva, String> {

}
