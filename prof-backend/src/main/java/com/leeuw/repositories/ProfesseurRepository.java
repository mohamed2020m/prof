package com.leeuw.repositories;

import com.leeuw.entities.Professeur;
import com.leeuw.entities.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {
    List<Professeur> findBySpecialite(Specialite specialite);
    List<Professeur> findByDateEmbaucheBetween(Date dateDebut, Date dateFin);
}
