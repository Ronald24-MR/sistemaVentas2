/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import entidades.Detalleventas;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Producto;
import entidades.Ventas;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class DetalleventasJpaController implements Serializable {

    public DetalleventasJpaController() {
        this.emf = Persistence.createEntityManagerFactory("sistemaVentas2PU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalleventas detalleventas) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto productoidProducto = detalleventas.getProductoidProducto();
            if (productoidProducto != null) {
                productoidProducto = em.getReference(productoidProducto.getClass(), productoidProducto.getIdProducto());
                detalleventas.setProductoidProducto(productoidProducto);
            }
            Ventas ventasidVentas = detalleventas.getVentasidVentas();
            if (ventasidVentas != null) {
                ventasidVentas = em.getReference(ventasidVentas.getClass(), ventasidVentas.getIdVentas());
                detalleventas.setVentasidVentas(ventasidVentas);
            }
            em.persist(detalleventas);
            if (productoidProducto != null) {
                productoidProducto.getDetalleventasCollection().add(detalleventas);
                productoidProducto = em.merge(productoidProducto);
            }
            if (ventasidVentas != null) {
                ventasidVentas.getDetalleventasCollection().add(detalleventas);
                ventasidVentas = em.merge(ventasidVentas);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalleventas detalleventas) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalleventas persistentDetalleventas = em.find(Detalleventas.class, detalleventas.getIdDetalleVentas());
            Producto productoidProductoOld = persistentDetalleventas.getProductoidProducto();
            Producto productoidProductoNew = detalleventas.getProductoidProducto();
            Ventas ventasidVentasOld = persistentDetalleventas.getVentasidVentas();
            Ventas ventasidVentasNew = detalleventas.getVentasidVentas();
            if (productoidProductoNew != null) {
                productoidProductoNew = em.getReference(productoidProductoNew.getClass(), productoidProductoNew.getIdProducto());
                detalleventas.setProductoidProducto(productoidProductoNew);
            }
            if (ventasidVentasNew != null) {
                ventasidVentasNew = em.getReference(ventasidVentasNew.getClass(), ventasidVentasNew.getIdVentas());
                detalleventas.setVentasidVentas(ventasidVentasNew);
            }
            detalleventas = em.merge(detalleventas);
            if (productoidProductoOld != null && !productoidProductoOld.equals(productoidProductoNew)) {
                productoidProductoOld.getDetalleventasCollection().remove(detalleventas);
                productoidProductoOld = em.merge(productoidProductoOld);
            }
            if (productoidProductoNew != null && !productoidProductoNew.equals(productoidProductoOld)) {
                productoidProductoNew.getDetalleventasCollection().add(detalleventas);
                productoidProductoNew = em.merge(productoidProductoNew);
            }
            if (ventasidVentasOld != null && !ventasidVentasOld.equals(ventasidVentasNew)) {
                ventasidVentasOld.getDetalleventasCollection().remove(detalleventas);
                ventasidVentasOld = em.merge(ventasidVentasOld);
            }
            if (ventasidVentasNew != null && !ventasidVentasNew.equals(ventasidVentasOld)) {
                ventasidVentasNew.getDetalleventasCollection().add(detalleventas);
                ventasidVentasNew = em.merge(ventasidVentasNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleventas.getIdDetalleVentas();
                if (findDetalleventas(id) == null) {
                    throw new NonexistentEntityException("The detalleventas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalleventas detalleventas;
            try {
                detalleventas = em.getReference(Detalleventas.class, id);
                detalleventas.getIdDetalleVentas();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleventas with id " + id + " no longer exists.", enfe);
            }
            Producto productoidProducto = detalleventas.getProductoidProducto();
            if (productoidProducto != null) {
                productoidProducto.getDetalleventasCollection().remove(detalleventas);
                productoidProducto = em.merge(productoidProducto);
            }
            Ventas ventasidVentas = detalleventas.getVentasidVentas();
            if (ventasidVentas != null) {
                ventasidVentas.getDetalleventasCollection().remove(detalleventas);
                ventasidVentas = em.merge(ventasidVentas);
            }
            em.remove(detalleventas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detalleventas> findDetalleventasEntities() {
        return findDetalleventasEntities(true, -1, -1);
    }

    public List<Detalleventas> findDetalleventasEntities(int maxResults, int firstResult) {
        return findDetalleventasEntities(false, maxResults, firstResult);
    }

    private List<Detalleventas> findDetalleventasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalleventas.class));
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

    public Detalleventas findDetalleventas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalleventas.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleventasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalleventas> rt = cq.from(Detalleventas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
