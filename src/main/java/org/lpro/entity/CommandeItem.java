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
    private String id;

    @NotNull
    private long sandwich, taille;

    @NotNull
    private int quantity;

    @ManyToMany(mappedBy="commandeItem")
    private Set<Commande> commande = new HashSet<Commande>();

    public CommandeItem() {

    }

    public CommandeItem(long sandwich, long taille, int quantity) {
        this.sandwich = sandwich;
        this.taille = taille;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSandwich() {
        return sandwich;
    }

    public void setSandwich(long sandwich) {
        this.sandwich = sandwich;
    }

    public long getTaille() {
        return taille;
    }

    public void setTaille(long taille) {
        this.taille = taille;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Set<Commande> getCommande() {
        return commande;
    }

    public void setCommande(Set<Commande> commande) {
        this.commande = commande;
    }
}