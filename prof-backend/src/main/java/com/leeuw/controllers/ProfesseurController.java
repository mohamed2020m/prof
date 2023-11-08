package com.leeuw.controllers;

import com.leeuw.entities.Professeur;
import com.leeuw.entities.Specialite;
import com.leeuw.services.ProfesseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/professeurs")
@CrossOrigin
public class ProfesseurController {
    @Autowired
    private ProfesseurService professeurService;

    @GetMapping
    public List<Professeur> findAllProfesseur(){
        return professeurService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        Professeur Professeur = professeurService.findById(id);
        if(Professeur == null) {
            return new ResponseEntity<Object>("Professeur with ID " + id + " not found", HttpStatus.BAD_REQUEST);
        }
        else {
            return ResponseEntity.ok(Professeur);
        }
    }

    @PostMapping
    public Professeur createProfesseur(@RequestBody Professeur Professeur) {
        Professeur.setId(0L);
        return professeurService.create(Professeur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProfesseur(@PathVariable Long id, @RequestBody Professeur Professeur) {
//		Professeur Professeur = professeurService.findById(id);
        if(professeurService.findById(id) == null) {
            return new ResponseEntity<Object>("Professeur with ID " + id + " not found", HttpStatus.BAD_REQUEST);
        }
        else {
            Professeur.setId(id);
//			return ResponseEntity.ok(professeurService.update(Professeur));
            professeurService.update(Professeur);
            return new ResponseEntity<>("{\"message\": \"UPDATE AVEC SUCCES\"}", HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProfesseur(@PathVariable Long id){
        Professeur Professeur = professeurService.findById(id);
        if(Professeur == null) {
            return new ResponseEntity<Object>("Professeur with ID " + id + " not found", HttpStatus.BAD_REQUEST);
        }
        else {
            professeurService.delete(Professeur);
            return ResponseEntity.ok("Professeur has been deleted");
        }
    }

    @PostMapping("/specialite")
    public List<Professeur> findProfesseurBySpecialite(@RequestBody Specialite specialite) {
        return professeurService.findBySpecialite(specialite);
    }

    @GetMapping("/filterByDate")
    public List<Professeur> findByDateEmbaucheBetween(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin
    ) {
        return professeurService.findByDateEmbaucheBetween(dateDebut, dateFin);
    }
}
