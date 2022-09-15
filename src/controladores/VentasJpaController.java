/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Cliente;
import entidades.Vendedor;
import entidades.Ventas;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class VentasJpaController implements Serializable {

    public VentasJpaController() {
        this.emf = Persistence.createEntityManagerFactory("sistemaVentas2PU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ventas ventas) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente clienteidCliente = ventas.getClienteidCliente();
            if (clienteidCliente != null) {
                clienteidCliente = em.getReference(clienteidCliente.getClass(), clienteidCliente.getIdCliente());
                ventas.setClienteidCliente(clienteidCliente);
            }
            Vendedor vendedoridVendedor = ventas.getVendedoridVendedor();
            if (vendedoridVendedor != null) {
                vendedoridVendedor = em.getReference(vendedoridVendedor.getClass(), vendedoridVendedor.getIdVendedor());
                ventas.setVendedoridVendedor(vendedoridVendedor);
            }
            em.persist(ventas);
            if (clienteidCliente != null) {
                clienteidCliente.getVentasCollection().add(ventas);
                clienteidCliente = em.merge(clienteidCliente);
            }
            if (vendedoridVendedor != null) {
                vendedoridVendedor.getVentasCollection().add(ventas);
                vendedoridVendedor = em.merge(vendedoridVendedor);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ventas ventas) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ventas persistentVentas = em.find(Ventas.class, ventas.getIdVentas());
            Cliente clienteidClienteOld = persistentVentas.getClienteidCliente();
            Cliente clienteidClienteNew = ventas.getClienteidCliente();
            Vendedor vendedoridVendedorOld = persistentVentas.getVendedoridVendedor();
            Vendedor vendedoridVendedorNew = ventas.getVendedoridVendedor();
            if (clienteidClienteNew != null) {
                clienteidClienteNew = em.getReference(clienteidClienteNew.getClass(), clienteidClienteNew.getIdCliente());
                ventas.setClienteidCliente(clienteidClienteNew);
            }
            if (vendedoridVendedorNew != null) {
                vendedoridVendedorNew = em.getReference(vendedoridVendedorNew.getClass(), vendedoridVendedorNew.getIdVendedor());
                ventas.setVendedoridVendedor(vendedoridVendedorNew);
            }
            ventas = em.merge(ventas);
            if (clienteidClienteOld != null && !clienteidClienteOld.equals(clienteidClienteNew)) {
                clienteidClienteOld.getVentasCollection().remove(ventas);
                clienteidClienteOld = em.merge(clienteidClienteOld);
            }
            if (clienteidClienteNew != null && !clienteidClienteNew.equals(clienteidClienteOld)) {
                clienteidClienteNew.getVentasCollection().add(ventas);
                clienteidClienteNew = em.merge(clienteidClienteNew);
            }
            if (vendedoridVendedorOld != null && !vendedoridVendedorOld.equals(vendedoridVendedorNew)) {
                vendedoridVendedorOld.getVentasCollection().remove(ventas);
                vendedoridVendedorOld = em.merge(vendedoridVendedorOld);
            }
            if (vendedoridVendedorNew != null && !vendedoridVendedorNew.equals(vendedoridVendedorOld)) {
                vendedoridVendedorNew.getVentasCollection().add(ventas);
                vendedoridVendedorNew = em.merge(vendedoridVendedorNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ventas.getIdVentas();
                if (findVentas(id) == null) {
                    throw new NonexistentEntityException("The ventas with id " + id + " no longer exists.");
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
            Ventas ventas;
            try {
                ventas = em.getReference(Ventas.class, id);
                ventas.getIdVentas();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ventas with id " + id + " no longer exists.", enfe);
            }
            Cliente clienteidCliente = ventas.getClienteidCliente();
            if (clienteidCliente != null) {
                clienteidCliente.getVentasCollection().remove(ventas);
                clienteidCliente = em.merge(clienteidCliente);
            }
            Vendedor vendedoridVendedor = ventas.getVendedoridVendedor();
            if (vendedoridVendedor != null) {
                vendedoridVendedor.getVentasCollection().remove(ventas);
                vendedoridVendedor = em.merge(vendedoridVendedor);
            }
            em.remove(ventas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ventas> findVentasEntities() {
        return findVentasEntities(true, -1, -1);
    }

    public List<Ventas> findVentasEntities(int maxResults, int firstResult) {
        return findVentasEntities(false, maxResults, firstResult);
    }

    private List<Ventas> findVentasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ventas.class));
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

    public Ventas findVentas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ventas.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ventas> rt = cq.from(Ventas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
