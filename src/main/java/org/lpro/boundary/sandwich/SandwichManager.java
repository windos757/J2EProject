package org.lpro.boundary.sandwich;

import org.lpro.entity.Sandwich;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class SandwichManager {

    @PersistenceContext
    EntityManager em;

    public Sandwich findById(long id) {
        return this.em.find(Sandwich.class, id);
    }

    public List<Sandwich> findAll() {
        Query q = this.em.createNamedQuery("Sandwich.findAll", Sandwich.class);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }

    public Sandwich save(Sandwich sandwich) {
        return this.em.merge(sandwich);
    }

    public void delete(long id) {
        try {
            Sandwich ref = this.em.getReference(Sandwich.class, id);
            this.em.remove(ref);
        } catch (EntityNotFoundException enfe) {
            // rien à faire
        }
    }
}