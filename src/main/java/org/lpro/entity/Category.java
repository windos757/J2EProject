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
@NamedQuery(name="Category.findAll", query = "SELECT c FROM Category c")
public class Category implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String nom;

    @NotNull
    private String descr;
    
    @ManyToMany
    private Set<Sandwich> sandwich = new HashSet<Sandwich>();

    public Category() {

    }

    public Category(long id, String nom, String descr) {
        this.id = id;
        this.nom = nom;
        this.descr = descr;
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

    public String getDescr() {
        return this.descr;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

}