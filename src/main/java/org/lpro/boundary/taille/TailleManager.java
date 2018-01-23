package org.lpro.boundary.taille;

import org.lpro.entity.Taille;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

@Stateless
public class TailleManager {

    @PersistenceContext
    EntityManager em;

    public Taille findById(long id) {
        return this.em.find(Taille.class, id);
    }

    public List<Taille> findAll() {
        Query q = this.em.createNamedQuery("Taille.findAll", Taille.class);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }

    public Taille save(Taille Taille) {
        return this.em.merge(Taille);
    }

    public void delete(long id) {
        try {
            Taille ref = this.em.getReference(Taille.class, id);
            this.em.remove(ref);
        } catch (EntityNotFoundException enfe) {
            // rien à faire
        }
    }
}