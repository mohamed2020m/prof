package com.leeuw.controllers;

import com.leeuw.entities.Specialite;
import com.leeuw.services.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/spcialites")
@CrossOrigin
public class SpcialiteController {
    @Autowired
    private SpecialiteService specialiteService;

    @GetMapping
    public List<Specialite> findAllSpecialite(){
        return specialiteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        Specialite Specialite = specialiteService.findById(id);
        if(Specialite == null) {
            return new ResponseEntity<Object>("Specialite with ID " + id + " not found", HttpStatus.BAD_REQUEST);
        }
        else {
            return ResponseEntity.ok(Specialite);
        }
    }

    @PostMapping
    public Specialite createSpecialite(@RequestBody Specialite Specialite) {
        Specialite.setId(0L);
        return specialiteService.create(Specialite);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateSpecialite(@PathVariable Long id, @RequestBody Specialite Specialite) {
//		Specialite Specialite = specialiteService.findById(id);
        if(specialiteService.findById(id) == null) {
            return new ResponseEntity<Object>("Specialite with ID " + id + " not found", HttpStatus.BAD_REQUEST);
        }
        else {
            Specialite.setId(id);
//			return ResponseEntity.ok(specialiteService.update(Specialite));
            specialiteService.update(Specialite);
            return new ResponseEntity<>("{\"message\": \"UPDATE AVEC SUCCES\"}", HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSpecialite(@PathVariable Long id){
        Specialite Specialite = specialiteService.findById(id);
        if(Specialite == null) {
            return new ResponseEntity<Object>("Specialite with ID " + id + " not found", HttpStatus.BAD_REQUEST);
        }
        else {
            specialiteService.delete(Specialite);
            return ResponseEntity.ok("Specialite has been deleted");
        }
    }
}
