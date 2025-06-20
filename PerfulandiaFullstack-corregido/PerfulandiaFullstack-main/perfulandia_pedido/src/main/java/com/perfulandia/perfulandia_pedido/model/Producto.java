package com.perfulandia.perfulandia_pedido.model;

import lombok.Data;

@Data
public class Producto {
    private int id;
    private String nombre;
    private int stock;
    private double precio;
}