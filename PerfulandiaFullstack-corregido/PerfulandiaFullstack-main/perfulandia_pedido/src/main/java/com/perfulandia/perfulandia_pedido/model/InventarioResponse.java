package com.perfulandia.perfulandia_pedido.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponse {
    private Long id;
    private Long productoId;
    private Integer cantidadDisponible;
    private String ubicacion;
}
