package com.perfulandia.pago.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Modelo HATEOAS para Pago
 * 
 * Este modelo extiende RepresentationModel para incluir enlaces navegables
 * que permiten a los clientes descubrir y navegar por la API de manera
 * dinámica, siguiendo los principios de HATEOAS.
 */
@Relation(collectionRelation = "pagos", itemRelation = "pago")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoModel extends RepresentationModel<PagoModel> {
    
    private Long id;
    private Long pedidoId;
    private Double monto;
    private String metodo;
    private String estado;
    
    // Constructor por defecto
    public PagoModel() {}
    
    // Constructor con todos los campos
    public PagoModel(Long id, Long pedidoId, Double monto, String metodo, String estado) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
    }
    
    // Constructor desde entidad Pago
    public PagoModel(Pago pago) {
        this.id = pago.getId();
        this.pedidoId = pago.getPedidoId();
        this.monto = pago.getMonto();
        this.metodo = pago.getMetodo();
        this.estado = pago.getEstado();
    }
    
    // Método para convertir a entidad Pago
    public Pago toEntity() {
        Pago pago = new Pago();
        pago.setId(this.id);
        pago.setPedidoId(this.pedidoId);
        pago.setMonto(this.monto);
        pago.setMetodo(this.metodo);
        pago.setEstado(this.estado);
        return pago;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPedidoId() {
        return pedidoId;
    }
    
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }
    
    public Double getMonto() {
        return monto;
    }
    
    public void setMonto(Double monto) {
        this.monto = monto;
    }
    
    public String getMetodo() {
        return metodo;
    }
    
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
} 