package com.leeuw.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Specialite")
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String libelle;

    public Specialite() {
    }

    public Specialite(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
