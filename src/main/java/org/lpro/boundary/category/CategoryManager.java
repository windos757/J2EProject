package org.lpro.boundary.category;

import io.swagger.annotations.Api;
import org.lpro.entity.Category;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class CategoryManager {

    @PersistenceContext
    EntityManager em;

    public Category findById(long id) {
        return this.em.find(Category.class, id);
    }

    public List<Category> findAll() {
        Query q = this.em.createNamedQuery("Category.findAll", Category.class);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList(); 
    }

    public Category save(Category category) {
        return this.em.merge(category);
    }

    public void delete(long id) {
        try {
            Category ref = this.em.getReference(Category.class, id);
            this.em.remove(ref);
        } catch (EntityNotFoundException enfe) {
            // rien à faire
        }
    }
}