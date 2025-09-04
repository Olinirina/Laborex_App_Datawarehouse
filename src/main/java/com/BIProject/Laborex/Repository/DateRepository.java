package com.BIProject.Laborex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.DatePerso;



@Repository
public interface DateRepository extends JpaRepository<DatePerso, String> {

}

