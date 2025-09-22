package com.BIProject.Laborex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BIProject.Laborex.Entity.Client;



@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

}
