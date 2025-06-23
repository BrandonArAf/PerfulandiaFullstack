package com.perfulandia.perfulandia_pedido.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {
    private Long pedidoId;
    private Double monto;
    private String metodo;
    private String estado;
}
