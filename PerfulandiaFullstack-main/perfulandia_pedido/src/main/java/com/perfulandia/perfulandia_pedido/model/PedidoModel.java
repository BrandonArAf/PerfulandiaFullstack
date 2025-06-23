package com.perfulandia.perfulandia_pedido.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;

/**
 * Modelo HATEOAS para Pedido
 * 
 * Este modelo extiende RepresentationModel para incluir enlaces navegables
 * que permiten a los clientes descubrir y navegar por la API de manera
 * dinámica, siguiendo los principios de HATEOAS.
 */
@Relation(collectionRelation = "pedidos", itemRelation = "pedido")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoModel extends RepresentationModel<PedidoModel> {
    
    private int id;
    private String cliente;
    private String producto;
    private int cantidad;
    private double total;
    private Date fecha;
    
    // Constructor por defecto
    public PedidoModel() {}
    
    // Constructor con todos los campos
    public PedidoModel(int id, String cliente, String producto, int cantidad, double total, Date fecha) {
        this.id = id;
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.total = total;
        this.fecha = fecha;
    }
    
    // Constructor desde entidad Pedido
    public PedidoModel(Pedido pedido) {
        this.id = pedido.getId();
        this.cliente = pedido.getCliente();
        this.producto = pedido.getProducto();
        this.cantidad = pedido.getCantidad();
        this.total = pedido.getTotal();
        this.fecha = pedido.getFecha();
    }
    
    // Método para convertir a entidad Pedido
    public Pedido toEntity() {
        Pedido pedido = new Pedido();
        pedido.setId(this.id);
        pedido.setCliente(this.cliente);
        pedido.setProducto(this.producto);
        pedido.setCantidad(this.cantidad);
        pedido.setTotal(this.total);
        pedido.setFecha(this.fecha);
        return pedido;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCliente() {
        return cliente;
    }
    
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
    
    public String getProducto() {
        return producto;
    }
    
    public void setProducto(String producto) {
        this.producto = producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
} 