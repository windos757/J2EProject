package org.lpro.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="Sandwich.findAll", query = "SELECT c FROM Sandwich c")
public class Sandwich {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String nom;

    @NotNull
    private String description;

    @NotNull
    private String type_pain;

    @NotNull
    private String img;

    public Sandwich() {

    }

    public Sandwich(long id, String nom, String description, String type_pain, String img) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.type_pain = type_pain ;
        this.img = img ;
    }

    public long getId() {
        return this.id;
    }

    public String getNom() {
        return this.nom;
    }

    public String getDescr() {
        return this.description;
    }

    public String getType_pain() { return this.type_pain; }

    public String getImg() { return this.img; }

    public void setId(long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescr(String descr) {
        this.description = descr;
    }

    public void setType_pain(String type_pain) { this.type_pain = type_pain; }

    public void setImg(String img) { this.img = img; }


}