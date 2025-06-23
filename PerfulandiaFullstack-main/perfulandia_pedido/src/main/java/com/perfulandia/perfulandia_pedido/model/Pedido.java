package com.perfulandia.perfulandia_pedido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String cliente;
    private String producto;
    private int cantidad;
    private double total;
    @Temporal(TemporalType.DATE)
    private Date fecha;
}
