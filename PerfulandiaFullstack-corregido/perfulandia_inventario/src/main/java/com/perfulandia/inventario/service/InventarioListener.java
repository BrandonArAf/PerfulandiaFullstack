package com.perfulandia.inventario.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventarioListener {

    @Autowired
    private InventarioService inventarioService;

    @RabbitListener(queues = "pedido-inventario")
    public void recibirMensaje(String mensaje) {
        System.out.println("Mensaje recibido por RabbitMQ: " + mensaje);
        String[] partes = mensaje.split(":");
        Long productoId = Long.parseLong(partes[0]);
        int cantidad = Integer.parseInt(partes[1]);
        inventarioService.ajustarCantidad(productoId, -cantidad);
        System.out.println("Descontado " + cantidad + " del producto " + productoId + " por RabbitMQ");
    }
}
