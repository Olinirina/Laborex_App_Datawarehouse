package com.BIProject.Laborex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.Article;



@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

}

