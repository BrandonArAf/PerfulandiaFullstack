package com.perfulandia.perfulandia_pedido.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponse {
    private Long id;
    private Long productoId;
    private Integer cantidadDisponible;
    private String ubicacion;
}
