package com.BIProject.Laborex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.Promotion;


@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String>{

}

