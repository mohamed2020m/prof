package me.ensa.professeur.classes;

import java.util.Date;

public class Professeur {
    private int id;
    private String lastName;
    private String firstName;
    private String tel;
    private String email;
    private Date dateEmbauche;
    private Specialite specialite;

    public Professeur() {
    }

    public Professeur(String lastName, String firstName, String tel, String email, Date dateEmbauche) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.tel = tel;
        this.email = email;
        this.dateEmbauche = dateEmbauche;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(Date dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    @Override
    public String toString() {
        return "Professeur{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", tel='" + tel + '\'' +
                ", email='" + email + '\'' +
                ", dateEmbauche=" + dateEmbauche +
                ", specialite=" + specialite +
                '}';
    }
}
