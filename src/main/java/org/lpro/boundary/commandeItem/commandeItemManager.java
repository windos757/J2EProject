package org.lpro.boundary.commandeItem;

import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.lpro.entity.CommandeItem;

@Stateless
@Transactional
public class CommandeItemManager {

    @PersistenceContext
    CommandeItem ci;

    public CommandeItem findById(String id) {
        return this.em.find(CommandeItem.class, id);
    }

    public List<CommandeItem> findAll() {
        Query q = this.em.createQuery("SELECT c FROM CommandeItem c");
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }

    public CommandeItem save(CommandeItem c) {
        c.setId(UUID.randomUUID().toString());
        // Persister la commande dans la BD
        return this.em.merge(c);
    }
}
