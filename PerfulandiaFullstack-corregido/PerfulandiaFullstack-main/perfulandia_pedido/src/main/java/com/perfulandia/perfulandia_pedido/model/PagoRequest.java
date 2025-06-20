package com.perfulandia.perfulandia_pedido.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {
    private Long pedidoId;
    private Double monto;
    private String metodo;
    private String estado;
}
