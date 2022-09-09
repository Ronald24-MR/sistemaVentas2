/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Detalleventas;
import entidades.Producto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class ProductoJpaController implements Serializable {

    public ProductoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("sistemaVentas2PU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) {
        if (producto.getDetalleventasCollection() == null) {
            producto.setDetalleventasCollection(new ArrayList<Detalleventas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Detalleventas> attachedDetalleventasCollection = new ArrayList<Detalleventas>();
            for (Detalleventas detalleventasCollectionDetalleventasToAttach : producto.getDetalleventasCollection()) {
                detalleventasCollectionDetalleventasToAttach = em.getReference(detalleventasCollectionDetalleventasToAttach.getClass(), detalleventasCollectionDetalleventasToAttach.getIdDetalleVentas());
                attachedDetalleventasCollection.add(detalleventasCollectionDetalleventasToAttach);
            }
            producto.setDetalleventasCollection(attachedDetalleventasCollection);
            em.persist(producto);
            for (Detalleventas detalleventasCollectionDetalleventas : producto.getDetalleventasCollection()) {
                Producto oldProductoidProductoOfDetalleventasCollectionDetalleventas = detalleventasCollectionDetalleventas.getProductoidProducto();
                detalleventasCollectionDetalleventas.setProductoidProducto(producto);
                detalleventasCollectionDetalleventas = em.merge(detalleventasCollectionDetalleventas);
                if (oldProductoidProductoOfDetalleventasCollectionDetalleventas != null) {
                    oldProductoidProductoOfDetalleventasCollectionDetalleventas.getDetalleventasCollection().remove(detalleventasCollectionDetalleventas);
                    oldProductoidProductoOfDetalleventasCollectionDetalleventas = em.merge(oldProductoidProductoOfDetalleventasCollectionDetalleventas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getIdProducto());
            Collection<Detalleventas> detalleventasCollectionOld = persistentProducto.getDetalleventasCollection();
            Collection<Detalleventas> detalleventasCollectionNew = producto.getDetalleventasCollection();
            List<String> illegalOrphanMessages = null;
            for (Detalleventas detalleventasCollectionOldDetalleventas : detalleventasCollectionOld) {
                if (!detalleventasCollectionNew.contains(detalleventasCollectionOldDetalleventas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalleventas " + detalleventasCollectionOldDetalleventas + " since its productoidProducto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Detalleventas> attachedDetalleventasCollectionNew = new ArrayList<Detalleventas>();
            for (Detalleventas detalleventasCollectionNewDetalleventasToAttach : detalleventasCollectionNew) {
                detalleventasCollectionNewDetalleventasToAttach = em.getReference(detalleventasCollectionNewDetalleventasToAttach.getClass(), detalleventasCollectionNewDetalleventasToAttach.getIdDetalleVentas());
                attachedDetalleventasCollectionNew.add(detalleventasCollectionNewDetalleventasToAttach);
            }
            detalleventasCollectionNew = attachedDetalleventasCollectionNew;
            producto.setDetalleventasCollection(detalleventasCollectionNew);
            producto = em.merge(producto);
            for (Detalleventas detalleventasCollectionNewDetalleventas : detalleventasCollectionNew) {
                if (!detalleventasCollectionOld.contains(detalleventasCollectionNewDetalleventas)) {
                    Producto oldProductoidProductoOfDetalleventasCollectionNewDetalleventas = detalleventasCollectionNewDetalleventas.getProductoidProducto();
                    detalleventasCollectionNewDetalleventas.setProductoidProducto(producto);
                    detalleventasCollectionNewDetalleventas = em.merge(detalleventasCollectionNewDetalleventas);
                    if (oldProductoidProductoOfDetalleventasCollectionNewDetalleventas != null && !oldProductoidProductoOfDetalleventasCollectionNewDetalleventas.equals(producto)) {
                        oldProductoidProductoOfDetalleventasCollectionNewDetalleventas.getDetalleventasCollection().remove(detalleventasCollectionNewDetalleventas);
                        oldProductoidProductoOfDetalleventasCollectionNewDetalleventas = em.merge(oldProductoidProductoOfDetalleventasCollectionNewDetalleventas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = producto.getIdProducto();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getIdProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detalleventas> detalleventasCollectionOrphanCheck = producto.getDetalleventasCollection();
            for (Detalleventas detalleventasCollectionOrphanCheckDetalleventas : detalleventasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Detalleventas " + detalleventasCollectionOrphanCheckDetalleventas + " in its detalleventasCollection field has a non-nullable productoidProducto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Producto findProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
