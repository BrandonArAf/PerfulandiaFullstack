package com.perfulandia.perfulandia_producto.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductoModel extends RepresentationModel<ProductoModel> {
    private int id;
    private String nombre;
    private int stock;
    private double precio;

    public ProductoModel(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.stock = producto.getStock();
        this.precio = producto.getPrecio();
    }
} 