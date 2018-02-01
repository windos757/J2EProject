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
@NamedQuery(name="CommandeItem.findAll", query = "SELECT c FROM CommandeItem c")
public class CommandeItem implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String sandwich, taille;

    @NotNull
    private Int quantity;

    @ManyToMany(mappedBy="commandeItem")
    private Set<Commande> commande = new HashSet<Commande>();

    public CommandeItem() {

    }

    public CommandeItem(String sandwich, String taille, Int quantity) {
        this.sandwich = sandwich;
        this.taille = taille;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSandwich() {
        return sandwich;
    }

    public void setSandwich(String sandwich) {
        this.sandwich = sandwich;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public Int getQuantity() {
        return quantity;
    }

    public void setQuantity(Int quantity) {
        this.quantity = quantity;
    }

    public Set<Commande> getCommande() {
        return commande;
    }

    public void setCommande(Set<Commande> commande) {
        this.commande = commande;
    }
}