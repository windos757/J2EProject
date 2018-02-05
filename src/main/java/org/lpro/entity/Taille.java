package org.lpro.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ManyToMany;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="Taille.findAll", query = "SELECT t FROM Taille t")
public class Taille implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String nom;

    @NotNull
    private Double prix;

    @ManyToMany
    private Set<Sandwich> sandwich = new HashSet<Sandwich>();

    public Taille() {

    }

    public Taille(long id, String nom, Double prix) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
    }

    public Set<Sandwich> getSandwich(){
        return this.sandwich;
    }

    public void setSandwich(Set<Sandwich> s){
        this.sandwich = s;
    }

    public long getId() {
        return this.id;
    }

    public String getNom() {
        return this.nom;
    }

    public Double getPrix() {
        return this.prix;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

}