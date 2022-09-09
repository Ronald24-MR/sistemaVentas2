/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ronald
 */
@Entity
@Table(name = "detalleventas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalleventas.findAll", query = "SELECT d FROM Detalleventas d")
    , @NamedQuery(name = "Detalleventas.findByIdDetalleVentas", query = "SELECT d FROM Detalleventas d WHERE d.idDetalleVentas = :idDetalleVentas")
    , @NamedQuery(name = "Detalleventas.findByCantidad", query = "SELECT d FROM Detalleventas d WHERE d.cantidad = :cantidad")
    , @NamedQuery(name = "Detalleventas.findByPrecioVenta", query = "SELECT d FROM Detalleventas d WHERE d.precioVenta = :precioVenta")})
public class Detalleventas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idDetalleVentas")
    private Integer idDetalleVentas;
    @Basic(optional = false)
    @Column(name = "Cantidad")
    private int cantidad;
    @Basic(optional = false)
    @Column(name = "PrecioVenta")
    private double precioVenta;
    @JoinColumn(name = "Producto_idProducto", referencedColumnName = "idProducto")
    @ManyToOne(optional = false)
    private Producto productoidProducto;
    @JoinColumn(name = "Ventas_idVentas", referencedColumnName = "idVentas")
    @ManyToOne(optional = false)
    private Ventas ventasidVentas;

    public Detalleventas() {
    }

    public Detalleventas(Integer idDetalleVentas) {
        this.idDetalleVentas = idDetalleVentas;
    }

    public Detalleventas(Integer idDetalleVentas, int cantidad, double precioVenta) {
        this.idDetalleVentas = idDetalleVentas;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
    }

    public Integer getIdDetalleVentas() {
        return idDetalleVentas;
    }

    public void setIdDetalleVentas(Integer idDetalleVentas) {
        this.idDetalleVentas = idDetalleVentas;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Producto getProductoidProducto() {
        return productoidProducto;
    }

    public void setProductoidProducto(Producto productoidProducto) {
        this.productoidProducto = productoidProducto;
    }

    public Ventas getVentasidVentas() {
        return ventasidVentas;
    }

    public void setVentasidVentas(Ventas ventasidVentas) {
        this.ventasidVentas = ventasidVentas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetalleVentas != null ? idDetalleVentas.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalleventas)) {
            return false;
        }
        Detalleventas other = (Detalleventas) object;
        if ((this.idDetalleVentas == null && other.idDetalleVentas != null) || (this.idDetalleVentas != null && !this.idDetalleVentas.equals(other.idDetalleVentas))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Detalleventas[ idDetalleVentas=" + idDetalleVentas + " ]";
    }
    
}
