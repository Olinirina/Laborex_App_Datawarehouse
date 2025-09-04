package com.BIProject.Laborex.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.Article;
import com.BIProject.Laborex.Entity.Comparaison;
import com.BIProject.Laborex.Entity.Concurrent;


@Repository
public interface ComparaisonRepository extends JpaRepository<Comparaison, Long> {
	
	Optional<Comparaison> findByArticleAndConcurrent(Article article, Concurrent concurrent);


}
