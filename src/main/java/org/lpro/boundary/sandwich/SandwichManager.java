package org.lpro.boundary.sandwich;

import org.lpro.entity.Sandwich;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

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

    public List<Sandwich> find(String ptype, int img, boolean paginate, int page, int nbPerPage){
        Query q = null ;
        if (ptype != null){
            if (img == 1){
                q =this.em.createNamedQuery("Sandwich.type.img", Sandwich.class);
                q.setParameter("type", ptype);
            }
            else{
                q =this.em.createNamedQuery("Sandwich.type", Sandwich.class);
                q.setParameter("type", ptype);
            }
        }
        else if(img == 1){
            q =this.em.createNamedQuery("Sandwich.img", Sandwich.class);
        }
        else{
            q =this.em.createNamedQuery("Sandwich.findAll", Sandwich.class);
        }
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);

        if (paginate == true){
            double nbSandwichs = q.getResultList().size();
            if (page <= 0) {
                page = 1;
            }
            else if (page > Math.ceil(nbSandwichs / (double) nbPerPage)) {
                page = (int) Math.ceil(nbSandwichs / (double) nbPerPage);
            }

            q.setFirstResult((page-1) * nbPerPage);
            q.setMaxResults(nbPerPage);
        }

        return q.getResultList();
    }


    public JsonObject getMetaPerPage(long size, String ptype, int img, int page, int nbPerPage) {
        return Json.createObjectBuilder()
                .add("count", ((size == -1) ? this.find(ptype, img,false, page, nbPerPage).size() : size))
                .add("size", this.find(ptype, img,true, page, nbPerPage).size())
                .build();
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